package org.firstinspires.ftc.teamcode.OpModes.Teleop.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Bot;
import org.firstinspires.ftc.teamcode.Helpers.Toggle;

public class TeleopDriver2 {
    private final Gamepad gamepad;

    // Toggles
    private final Toggle specimenClawToggle = new Toggle(false);

    // Variables
    private boolean manualSlideArmControl = false;

    private TeleopDriver2(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    public static TeleopDriver2 createInstance(Gamepad gamepad) {
        return new TeleopDriver2(gamepad);
    }

    public void loopGamepad() {
        runSpecimenArm();
        toggleSpecimenClaw();
        controlClawRotation();
        driveSlideArm();
        manualSlideArmControl();
        controlOCGBox();
        handleResetSlideEncoders();
    }

    private void runSpecimenArm() {
        if (gamepad.dpad_down) {
            Bot.specimenArm.movePrepHang(0.65);
        } else if (gamepad.dpad_left) {
            Bot.specimenArm.moveToCollect(0.4);
        }
    }

    private void toggleSpecimenClaw() {
        specimenClawToggle.toggle(gamepad.circle);
        if (specimenClawToggle.justChanged() && specimenClawToggle.get()) {
            Bot.specimenArm.openClaw();
        } else if (specimenClawToggle.justChanged() && !specimenClawToggle.get()) {
            Bot.specimenArm.closeClaw();
        }
    }

    private void controlClawRotation() {
        if (gamepad.left_stick_x <= -0.5) {
            Bot.intakeClaw.clawNeg90();
        } else if (gamepad.left_stick_x > -0.5 && gamepad.left_stick_x < -0.1) {
            Bot.intakeClaw.clawNeg45();
        } else if (gamepad.left_stick_x >= 0.5) {
            Bot.intakeClaw.clawPos90();
        } else if (gamepad.left_stick_x < 0.5 && gamepad.left_stick_x > 0.1) {
            Bot.intakeClaw.clawPos45();
        } else {
            Bot.intakeClaw.clawPos0();
        }
    }

    private void driveSlideArm() {
        if (gamepad.y) {
            manualSlideArmControl = false;
            Bot.intake.midMisumiWrist();
            Bot.slideArm.moveUp(0.65);
        } else if (gamepad.a) {
            manualSlideArmControl = false;
            Bot.intake.midMisumiWrist();
            Bot.slideArm.moveDown(0.65);
        } else if (gamepad.x) {
            manualSlideArmControl = false;
            Bot.intake.midMisumiWrist();
            Bot.slideArm.moveMid(0.65);
        }
        if (!manualSlideArmControl) {
            Bot.slideArm.update();
        }
    }

    private void manualSlideArmControl() {
        float rightStickY = -gamepad.right_stick_y;
        if (rightStickY > 0.25) {
            manualSlideArmControl = true;
            Bot.intake.midMisumiWrist();
            Bot.slideArm.move(rightStickY - 0.25);
        } else if (rightStickY < -0.25) {
            manualSlideArmControl = true;
            Bot.intake.midMisumiWrist();
            Bot.slideArm.move(rightStickY + 0.25);
        } else if (manualSlideArmControl) {
            Bot.slideArm.move(0);
        }
    }

    private void controlOCGBox() {
        if (gamepad.left_bumper) {
            Bot.ocgBox.ocgPitchUp();
        } else if (gamepad.right_bumper) {
            Bot.ocgBox.ocgPitchDrop();
        }
    }

    private void handleResetSlideEncoders() {
        if (gamepad.share && gamepad.options) {
            Bot.slideArm.resetEncoders();
        }
    }
}
