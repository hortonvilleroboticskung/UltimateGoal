package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple;
import com.qualcomm.robotcore.hardware.I2cWaitControl;
import com.qualcomm.robotcore.hardware.configuration.I2cSensor;
import com.qualcomm.robotcore.util.TypeConversion;

@SuppressWarnings({"WeakerAccess", "unused"})
@I2cSensor(name = "I2C DF Robotics Temperature & Distance Sensor", description = "DF Robotics I2C Temperature & Distance Sensor", xmlTag = "I2CTempDistSensor")
public class DFRoboticsGravityI2CTemperatureandDistanceSensor extends I2cDeviceSynchDevice<I2cDeviceSynchSimple> {

    public enum Register
    {
        DEVICE_ADDRESS(0x00),
        PRODUCT_ID(0x01),
        VERSION_NUMBER(0x02),
        DISTANCE_HIGH(0x03),
        DISTANCE_LOW(0x04),
        TEMPERATURE_HIGH(0x05),
        TEMPERATURE_LOW(0x06),
        CONFIGURE_REGISTERS(0x07),
        COMMAND_REGISTERS(0x08),
        LAST(COMMAND_REGISTERS.bVal);

        public int bVal;

        Register(int bVal)
        {
            this.bVal = bVal;
        }

    }



    public double getDistance()
    {
        short dataRaw = getDistanceRaw();

        // The first 3 bits are alert bits that we don't care about here. We need to force them to
        // be 0s or 1s if the number is positive or negative depending on the sign
        if((dataRaw & 0x1000) == 0x1000) // Negative
            dataRaw |= 0xE000;
        else // Positive
            dataRaw &= 0x1FFF;

        // Multiply by least significant bit (2^-4 = 1/16) to scale
        return dataRaw / 16.0;
    }

    public short getDistanceRaw()
    {
        return readShort(Register.DISTANCE_HIGH);
    }


    @Override
    protected synchronized boolean doInitialize()
    {
        deviceClient.enableWriteCoalescing(true);
        return true;
    }

    public void setI2cAddress(I2cAddr newAddress)
    {
        deviceClient.setI2cAddr(newAddress);
    }

    public void setI2cAddr(I2cAddr i2cAddr)
    {
        this.deviceClient.setI2cAddress(i2cAddr);
    }

    public void write(int ireg, byte[] data)
    {
        super.deviceClient.write(ireg, data);
    }

    public final static I2cAddr ADDRESS_I2C_DEFAULT = I2cAddr.create7bit(0x18);

    public DFRoboticsGravityI2CTemperatureandDistanceSensor(I2cDeviceSynch deviceClient)
    {
        super(deviceClient, true);
        this.deviceClient.setI2cAddress(ADDRESS_I2C_DEFAULT);
        super.registerArmingStateCallback(false);
    }

    @Override
    public Manufacturer getManufacturer()
    {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName()
    {
        return "DF Robotics Gravity I2C Temperature and Distance Sensor";
    }

    protected void writeShort(final Register reg, short value)
    {
        deviceClient.write(reg.bVal, TypeConversion.shortToByteArray(value));
    }

    protected short readShort(Register reg)
    {
        //creg is the number of bytes to read
        return TypeConversion.byteArrayToShort(deviceClient.read(reg.bVal, 2));
    }

}
