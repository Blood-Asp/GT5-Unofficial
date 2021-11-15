package gregtech.api.util;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;

import javax.annotation.Nonnull;

/**
 * We could well have used {@link java.io.Serializable}, but that's too slow and should generally be avoided
 */
public interface ISerializableObject {

    @Nonnull
    ISerializableObject copy();

    @Nonnull
    NBTBase saveDataToNBT();

    /**
     * Write data to given ByteBuf
     * The data saved this way is intended to be stored for short amount of time over network.
     * DO NOT store it to disks.
     */
    // the NBT is an unfortunate piece of tech. everything uses it but its API is not as efficient as could be
    void writeToByteBuf(ByteBuf aBuf);

    void loadDataFromNBT(NBTBase aNBT);

    /**
     * Read data from given parameter and return this.
     * The data read this way is intended to be stored for short amount of time over network.
     */
    // the NBT is an unfortunate piece of tech. everything uses it but its API is not as efficient as could be
    @Nonnull
    ISerializableObject readFromPacket(ByteArrayDataInput aBuf, EntityPlayerMP aPlayer);

    final class LegacyCoverData implements ISerializableObject {
        private int mData;

        public LegacyCoverData() {
        }

        public LegacyCoverData(int mData) {
            this.mData = mData;
        }

        @Override
        @Nonnull
        public ISerializableObject copy() {
            return new LegacyCoverData(mData);
        }

        @Override
        @Nonnull
        public NBTBase saveDataToNBT() {
            return new NBTTagInt(mData);
        }

        @Override
        public void writeToByteBuf(ByteBuf aBuf) {
            aBuf.writeInt(mData);
        }

        @Override
        public void loadDataFromNBT(NBTBase aNBT) {
            mData = aNBT instanceof NBTBase.NBTPrimitive ? ((NBTBase.NBTPrimitive) aNBT).func_150287_d() : 0;
        }

        @Override
        @Nonnull
        public ISerializableObject readFromPacket(ByteArrayDataInput aBuf, EntityPlayerMP aPlayer) {
            mData = aBuf.readInt();
            return this;
        }

        public int get() {
            return mData;
        }

        public void set(int mData) {
            this.mData = mData;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LegacyCoverData that = (LegacyCoverData) o;

            return mData == that.mData;
        }

        @Override
        public int hashCode() {
            return mData;
        }

        @Override
        public String toString() {
            return String.valueOf(mData);
        }
    }
}
