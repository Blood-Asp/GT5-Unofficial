package gregtech.api.enums;

public enum HeatingCoilLevel {
        None             (     0L),
        //        ULV              (   901L),  //Not implemented
        LV               ( 1_801L), //Cupronickel
        MV               ( 2_701L), //KANTHAL
        HV               ( 3_601L), //NICHROME
        EV               ( 4_501L), //TUNGSTENSTEEL
        IV               ( 5_401L), //HSSG
        //        LuV              ( 6_301L), //Not implemented
        ZPM              ( 7_201L), //NAQUADAH
        UV               ( 9_001L), //NAQUADAHALLOY
        UHV              ( 9_901L), //ELECTRUMFLUX
        UEV              (10_801L), //AWAKENEDDRACONIUM
        UIV              (11_701L),

        //Not Implemented yet
        UMV              (12_601L),
        UXV              (13_501L),
        OpV              (14_401L),
        MAX              (15_301L),
        ;

        private final long HEAT;

        HeatingCoilLevel(long heat) {
            this.HEAT = heat;
        }
        /**
         * @return the coil heat, used for recipes in the Electronic Blast Furnace for example
         */
        public long getHeat() {
            return HEAT;
        }

        /**
         * @return the coil tier, used for discount in the Pyrolyse Ofen for example
         */
        public byte getTier() {
            return (byte) (this.ordinal() - 1);
        }

        /**
         * @return the coil Level, used for Parallels in the Multi Furnace for example
         */
        public byte getLevel() {
            return (byte) Math.max(16, 2 << (this.ordinal() -1));
        }

        /**
         * @return the coil Discount, used for discount in the Multi Furnace for example
         */
        public byte getCostDiscount() {
            return (byte) Math.min(1, 2 << (this.ordinal() -1 -4)); //-1 bcs. of none, -4 = offset
        }

        public static HeatingCoilLevel getFromTier(byte tier){
            if (tier < 0 || tier > HeatingCoilLevel.values().length -1)
                return HeatingCoilLevel.None;

            return HeatingCoilLevel.values()[tier+1];
        }
}
