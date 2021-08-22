package gregtech.api.graphs.paths;

import gregtech.api.metatileentity.BaseMetaPipeEntity;
import gregtech.api.metatileentity.MetaPipeEntity;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Cable;
import net.minecraft.server.MinecraftServer;

//path for cables
//all calculations like amp and voltage happens here
public class PowerNodePath extends NodePath {
    int mMaxAmps;
    int mAmps = 0;
    int mLoss;
    int mVoltage = 0;
    int mMaxVoltage;
    int mTick = 0;
    boolean mCountUp = true;


    public PowerNodePath(MetaPipeEntity[] aCables) {
        super(aCables);
    }

    public int getLoss() {
        return mLoss;
    }

    public void applyVoltage(int aVoltage, boolean aCountUp) {
        int tNewTime = MinecraftServer.getServer().getTickCounter();
        if (mTick != tNewTime) {
            reset(tNewTime - mTick);
            mTick = tNewTime;
            this.mVoltage = aVoltage;
            this.mCountUp = aCountUp;
        } else if (this.mCountUp != aCountUp && (aVoltage - mLoss)> this.mVoltage || aVoltage > this.mVoltage){
            this.mCountUp = aCountUp;
            this.mVoltage = aVoltage;
        }
        if (aVoltage > mMaxVoltage) {
            for (MetaPipeEntity tCable : mPipes) {
                if (((GT_MetaPipeEntity_Cable)tCable).mVoltage < this.mVoltage) {
                    BaseMetaPipeEntity tBaseCable = (BaseMetaPipeEntity) tCable.getBaseMetaTileEntity();
                    if (tBaseCable != null) {
                        tBaseCable.setToFire();
                    }
                }
            }
        }
    }

    public void addAmps(int aAmps) {
        this.mAmps += aAmps;
        if (this.mAmps > mMaxAmps * 40) {
            for (MetaPipeEntity tCable : mPipes) {
                if (((GT_MetaPipeEntity_Cable)tCable).mAmperage*40 < this.mAmps) {
                    BaseMetaPipeEntity tBaseCable = (BaseMetaPipeEntity) tCable.getBaseMetaTileEntity();
                    if (tBaseCable != null) {
                        tBaseCable.setToFire();
                    }
                }
            }
        }
    }

    //if no amps pass trough for more then 0.5 second reduce them to minimize wrong results
    //but still allow the player to see if activity is happening
    public int getAmps() {
        int tTime = MinecraftServer.getServer().getTickCounter() - 10;
        if (mTick < tTime) {
            reset(tTime - mTick);
            mTick = tTime;
        }
        return mAmps;
    }

    public int getVoltage(MetaPipeEntity aCable) {
        int tLoss = 0;
        if (mCountUp) {
            for (int i = 0; i < mPipes.length; i++) {
                GT_MetaPipeEntity_Cable tCable = (GT_MetaPipeEntity_Cable) mPipes[i];
                tLoss += tCable.mCableLossPerMeter;
                if (aCable == tCable) {
                    return Math.max(mVoltage - tLoss, 0);
                }
            }
        } else {
            for (int i = mPipes.length - 1; i >= 0; i--) {
                GT_MetaPipeEntity_Cable tCable = (GT_MetaPipeEntity_Cable) mPipes[i];
                tLoss += tCable.mCableLossPerMeter;
                if (aCable == tCable) {
                    return Math.max(mVoltage - tLoss, 0);
                }
            }
        }
        return -1;
    }

    private void reset(int aTimePassed) {
        if (aTimePassed < 0 || aTimePassed > 100) {
            mAmps = 0;
        }
        mAmps = Math.max(0, mAmps - (mMaxAmps * aTimePassed));
    }

    @Override
    protected void processPipes() {
        super.processPipes();
        mMaxAmps = Integer.MAX_VALUE;
        mMaxVoltage = Integer.MAX_VALUE;
        for (MetaPipeEntity tCable : mPipes) {
            if (tCable instanceof GT_MetaPipeEntity_Cable) {
                mMaxAmps = Math.min((int)((GT_MetaPipeEntity_Cable) tCable).mAmperage, mMaxAmps);
                mLoss += (int)((GT_MetaPipeEntity_Cable) tCable).mCableLossPerMeter;
                mMaxVoltage = Math.min((int)((GT_MetaPipeEntity_Cable) tCable).mVoltage, mMaxVoltage);
            }
        }
    }
}
