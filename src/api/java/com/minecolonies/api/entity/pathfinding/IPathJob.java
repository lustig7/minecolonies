package com.minecolonies.api.entity.pathfinding;

import net.minecraft.world.level.pathfinder.Path;

import java.util.concurrent.Callable;

/**
 * Interface for path jobs
 */
public interface IPathJob extends Callable<Path>
{
    /**
     * Get the path result holder for this job
     * @return
     */
    PathResult getResult();

    /**
     * Get the pathing options used for this job
     * @return
     */
    public PathingOptions getPathingOptions();
}
