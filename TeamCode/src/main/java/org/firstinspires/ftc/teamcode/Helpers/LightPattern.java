package org.firstinspires.ftc.teamcode.Helpers;

import org.firstinspires.ftc.teamcode.Hardware.LEDController;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Class used to make the LEDs on the robot look cool.
 */
public class LightPattern {

    private final double cycleLength;
    private final double patternLength;
    private final BlinkinPattern[] Patterns;
    LEDController ledController;


    public LightPattern(long cycleLength, LinearOpMode opMode) {
        this.cycleLength = cycleLength;
        ledController = new LEDController(opMode);

        Patterns = new BlinkinPattern[]{
                BlinkinPattern.RAINBOW_RAINBOW_PALETTE,
                BlinkinPattern.SHOT_RED,
                BlinkinPattern.FIRE_LARGE,
                BlinkinPattern.COLOR_WAVES_RAINBOW_PALETTE,
                BlinkinPattern.BREATH_RED,
                BlinkinPattern.STROBE_RED};

        patternLength = this.cycleLength / Patterns.length;
    }

    /**
     * Called in the run loop.
     * Displays a pattern based on what tick
     * in the cycle (cycleLength) the robot is on
     * @param ticks
     */
    public void lights(long ticks) {
        double cycleTime = ticks % cycleLength;

        int patternNumber = (int) Math.floor(cycleTime / patternLength);

        ledController.lightsOn(Patterns[patternNumber]);
    }

}
