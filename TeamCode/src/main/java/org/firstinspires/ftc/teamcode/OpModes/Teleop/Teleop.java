package org.firstinspires.ftc.teamcode.OpModes.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Hardware.MecanumBase;

@TeleOp
public class Teleop extends LinearOpMode {
    public void runOpMode() {
        MecanumBase mecanumBase = new MecanumBase(this);

        waitForStart();

        while(opModeIsActive()) {
            double px = gamepad1.left_stick_x;
            double py = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;
            double stickAngle = Math.atan2(py, px);
            double speed = Math.sqrt((px * px + py * py));
            boolean aButton = gamepad1.a;
            boolean bButton = gamepad1.b;
            boolean xButton = gamepad1.x;
            boolean yButton = gamepad1.y;

            if(xButton) {mecanumBase.setNorthMode(true);}
            if(yButton) {mecanumBase.setNorthMode(false);}

            mecanumBase.move(speed, stickAngle, turn);

            // Telemetry in movement classes
            telemetry.update();
        }
    }
}
