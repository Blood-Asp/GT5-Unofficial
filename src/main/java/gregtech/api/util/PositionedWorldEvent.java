package gregtech.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class PositionedWorldEvent<T> {

    public PositionedWorldEvent() {}

    public PositionedWorldEvent(World world) {
        this.world = world;
    }

    public PositionedWorldEvent(Vec3 position) {
        this.position = position;
    }

    public PositionedWorldEvent(T thing) {
        this.thing = thing;
    }

    public PositionedWorldEvent(World world, T thing) {
        this(world);
        this.thing = thing;
    }

    public PositionedWorldEvent(World world, Vec3 position) {
        this(world);
        this.position = position;
    }

    public PositionedWorldEvent(Vec3 position, T thing) {
        this(position);
        this.thing = thing;
    }

    public PositionedWorldEvent(T thing, Vec3 position) {
        this(position, thing);
    }

    public PositionedWorldEvent(World world, Vec3 position, T thing) {
        this(world, position);
        this.thing = thing;
    }

    public PositionedWorldEvent(World world, T thing, Vec3 position) {
        this(world, position, thing);
    }

    private World world;
    private Vec3 position;
    private T thing;

    public World getWorld() {
        return world;
    }

    public PositionedWorldEvent<T> setWorld(World world) {
        this.world = world;
        return this;
    }

    public Vec3 getPosition() {
        return position;
    }

    public PositionedWorldEvent<T> setPosition(Vec3 position) {
        this.position = position;
        return this;
    }

    public PositionedWorldEvent<T> setPosition(double x, double y, double z) {
        this.position = Vec3.createVectorHelper(x, y, z);
        return this;
    }

    public T getThing() {
        return thing;
    }

    public PositionedWorldEvent<T> setThing(T thing) {
        this.thing = thing;
        return this;
    }

    public void spawnParticle(double motionX, double motionY, double motionZ) {
        if (position == null || !(thing instanceof String) || world == null)
            return;

        world.spawnParticle((String) thing, position.xCoord, position.yCoord, position.zCoord, motionX, motionY, motionZ);
    }

    public void playSoundEffect(float volume, float pitch) {
        if (position == null || !(thing instanceof String) || world == null)
            return;

        world.playSoundEffect(position.xCoord, position.yCoord, position.zCoord, (String) thing, volume, pitch);
    }

    public void playRecord() {
        if (position == null || !(thing instanceof String) || world == null)
            return;

        world.playRecord((String) thing, (int) position.xCoord, (int) position.yCoord, (int) position.zCoord);
    }

    public void playSound(float volume, float pitch, boolean proximity) {
        if (position == null || !(thing instanceof String) || world == null)
            return;

        world.playSound(position.xCoord, position.yCoord, position.zCoord, (String) thing, volume, pitch, proximity);
    }

    public void createExplosion(float strength, boolean isSmoking) {
        newExplosion(strength, false, isSmoking);
    }

    public void newExplosion(float strength, boolean isFlaming, boolean isSmoking) {
        if (position == null || world == null)
            return;

        world.newExplosion((Entity) thing, position.xCoord, position.yCoord, position.zCoord, strength, isFlaming, isSmoking);
    }

    public boolean extinguishFire(int side) {
        if (position == null || world == null)
            return false;

        return world.extinguishFire((EntityPlayer) thing, (int) position.xCoord, (int) position.yCoord, (int) position.zCoord, side);
    }

    public void playSoundAtEntity(Entity entity, float volume, float pitch) {
        if (!(thing instanceof String)) {
            return;
        }
        world.playSoundAtEntity(entity, (String) thing, volume, pitch);
    }

    public void playSoundToNearExcept(EntityPlayer player, float volume, float pitch) {
        if (!(thing instanceof String)) {
            return;
        }
        world.playSoundToNearExcept(player, (String) thing, volume, pitch);
    }

    public void times(int times, Consumer<PositionedWorldEvent<T>> action) {
        Objects.requireNonNull(action);
        for (int i = 0; i < times; i++) {
            action.accept(this);
        }
    }

    public void times(int times, BiConsumer<PositionedWorldEvent<T>, Integer> action) {
        Objects.requireNonNull(action);
        for (int i = 0; i < times; i++) {
            action.accept(this, i);
        }
    }
}