//package org.firstinspires.ftc.teamcode;
//
//
//import com.qualcomm.robotcore.hardware.I2cAddr;
//import com.qualcomm.robotcore.hardware.I2cDevice;
//import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
//import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
//import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple;
//import com.qualcomm.robotcore.hardware.configuration.I2cSensor;
//
//@SuppressWarnings({"WeakerAccess", "unused"})
//@I2cSensor(name = "I2C Temperature & Distance Sensor", description = "DF Robotics I2C Temperature & Distance Sensor", xmlTag = "I2CTempDistSensor")
//public class SlaveBus extends I2cDeviceSynchDevice<I2cDeviceSynchSimple> {
//
//    public static final I2cAddr I2C_ADDRESS = I2cAddr.create7bit(0x22);
//
//    @Override
//    protected boolean doInitialize() {
//        return false;
//    }
//
//    @Override
//    public Manufacturer getManufacturer() {
//        return null;
//    }
//
//    @Override
//    public String getDeviceName() {
//        return null;
//    }
//
//    public enum Register {
//        DEVICE_ADDRESS(0x00),
//        PRODUCT_ID(0x01),
//        VERSION_NUMBER(0x02),
//        DISTANCE_HIGH(0x03),
//        DISTANCE_LOW(0x04),
//        TEMPERATURE_HIGH(0x05),
//        TEMPERATURE_LOW(0x06),
//        CONFIGURE_REGISTERS(0x07),
//        COMMAND_REGISTERS(0x08),
//        LAST(COMMAND_REGISTERS.bVal);
//
//        public int bVal;
//
//        Register(int bVal) {
//            this.bVal = bVal;
//        }
//
//    }
//
//    public SlaveBus(I2cDeviceSynch deviceClient)
//    {
//        super(deviceClient, true);
//
//        this.deviceClient.setI2cAddress(I2C_ADDRESS);
//
//        super.registerArmingStateCallback(false);
////        this.deviceClient.engage();
//
//    }
//}
