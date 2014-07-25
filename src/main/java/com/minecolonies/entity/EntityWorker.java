package com.minecolonies.entity;

import com.minecolonies.entity.ai.EntityAIGoHome;
import com.minecolonies.entity.ai.EntityAISleep;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityWorker extends EntityCitizen
{
    private List<ItemStack> itemsNeeded = new ArrayList<ItemStack>();

    public EntityWorker(World world)
    {
        super(world);
    }

    @Override
    protected void initTasks()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityMob.class, 8.0F, 0.6D, 0.6D));
        this.tasks.addTask(2, new EntityAIGoHome(this));
        this.tasks.addTask(3, new EntityAISleep(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(6, new EntityAIWatchClosest2(this, EntityCitizen.class, 5.0F, 0.02F));
        this.tasks.addTask(7, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityLiving.class, 6.0F));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        if(!itemsNeeded.isEmpty())
        {
            NBTTagList itemsNeededTag = new NBTTagList();
            for(ItemStack itemstack : itemsNeeded)
            {
                NBTTagCompound itemCompound = new NBTTagCompound();
                itemstack.writeToNBT(itemCompound);
                itemsNeededTag.appendTag(itemCompound);
            }
            compound.setTag("itemsNeeded", itemsNeededTag);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        NBTTagList itemsNeededTag = compound.getTagList("itemsNeeded", Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < itemsNeededTag.tagCount(); i++)
        {
            NBTTagCompound itemCompound = itemsNeededTag.getCompoundTagAt(i);
            itemsNeeded.add(ItemStack.loadItemStackFromNBT(itemCompound));
        }
    }

    public abstract boolean isNeeded();

    public boolean hasItemsNeeded()
    {
        return itemsNeeded.isEmpty();
    }

    public List<ItemStack> getItemsNeeded()
    {
        return itemsNeeded;
    }

    public void addItemNeeded(ItemStack itemstack)
    {
        boolean isAlreadyNeeded = false;
        for(ItemStack neededItem : itemsNeeded)
        {
            if(itemstack.isItemEqual(neededItem))
            {
                for(int i = 0; i < itemstack.stackSize; i++)
                {
                    neededItem.stackSize++;
                }
                isAlreadyNeeded = true;
            }
        }
        if(!isAlreadyNeeded)
        {
            itemsNeeded.add(itemstack);
        }
    }

    public ItemStack removeItemNeeded(ItemStack itemstack)
    {
        ItemStack itemCopy = itemstack.copy();
        for(ItemStack neededItem : itemsNeeded)
        {
            if(itemCopy.isItemEqual(neededItem))
            {
                for(int i = 0; i < itemCopy.stackSize; i++)
                {
                    itemCopy.stackSize--;
                    neededItem.stackSize--;
                    if(neededItem.stackSize == 0)
                    {
                        itemsNeeded.remove(itemsNeeded.indexOf(neededItem));
                        break;
                    }
                }
            }
        }
        return itemCopy.stackSize == 0 ? null : itemstack;
    }
}
