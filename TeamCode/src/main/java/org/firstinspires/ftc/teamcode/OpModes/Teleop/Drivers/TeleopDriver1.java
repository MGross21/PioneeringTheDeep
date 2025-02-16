package org.firstinspires.ftc.teamcode.OpModes.Teleop.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Bot;
import org.firstinspires.ftc.teamcode.Helpers.Toggle;

public class TeleopDriver1 {
    private final Gamepad gamepad;
    private final ElapsedTime timer = new ElapsedTime();

    // Enums for state machine
    private enum TransferState {
        NONE,
        OCG_UP,
        WRIST_UP,
        DROP,
        OCG_IDLE,
        WRIST_DOWN
    }

    private enum IntakeState {
        NONE,
        MID_WRIST,
        EXTEND
    }

    // Variables
    private double speed = 0.5;
    private boolean northMode = true;
    private IntakeState intakeState = IntakeState.NONE;
    private TransferState transferState = TransferState.NONE;

    // Toggles
    private final Toggle northModeToggle = new Toggle(true);
    private final Toggle incSpeedToggle = new Toggle(false);
    private final Toggle decSpeedToggle = new Toggle(false);
    private final Toggle resetIMUToggle = new Toggle(false);
    private final Toggle extendIntakeToggle = new Toggle(false);
    private final Toggle intakeClawToggle = new Toggle(false);
    private final Toggle intakeWristToggle = new Toggle(false);
    private final Toggle intakeTransferToggle = new Toggle(false);

    private TeleopDriver1(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    public static TeleopDriver1 createInstance(Gamepad gamepad) {
        return new TeleopDriver1(gamepad);
    }

    public void loopGamepad() {
        handleSpeedControls();
        handleNorthMode();
        handleIMUReset();
        driveRobot();
        updateIntakeState();
        driveIntake();
        toggleIntakeClaw();
        toggleIntakeWrist();
        updateTransferState();
        handleTransfer();
    }

    private void handleSpeedControls() {
        incSpeedToggle.toggle(gamepad.right_bumper);
        decSpeedToggle.toggle(gamepad.left_bumper);
        if (incSpeedToggle.justChanged() && incSpeedToggle.get()) {
            speed += 0.1;
        }
        if (decSpeedToggle.justChanged() && decSpeedToggle.get()) {
            speed -= 0.1;
        }
    }

    private void handleNorthMode() {
        northModeToggle.toggle(gamepad.share && gamepad.options);
        northMode = northModeToggle.get();
        Bot.mecanumBase.setNorthMode(northMode);
    }

    private void handleIMUReset() {
        resetIMUToggle.toggle(gamepad.x);
        if (resetIMUToggle.justChanged() && resetIMUToggle.get()) {
            Bot.pinpoint.resetIMU();
        }
    }

    private void driveRobot() {
        double px = Math.pow(gamepad.left_stick_x, 3);
        double py = -Math.pow(gamepad.left_stick_y, 3);
        double turn = gamepad.right_stick_x;
        Bot.mecanumBase.move(px, py, turn, speed);
    }

    private void updateIntakeState() {
        // Toggle prevents the state being set multiple times if the button is held
        extendIntakeToggle.toggle(gamepad.dpad_up);
        if (extendIntakeToggle.justChanged() && extendIntakeToggle.get()) {
            timer.reset();
            intakeState = IntakeState.MID_WRIST;
        } else if (gamepad.dpad_down) {
            intakeState = IntakeState.NONE;
            Bot.intake.midMisumiWrist();
            Bot.intake.retractMisumiDrive();
            Bot.intakeClaw.clawUp();
        }
    }

    private void driveIntake() {
        switch (intakeState) {
            case NONE:
                break;
            case MID_WRIST:
                Bot.intake.midMisumiWrist();
                if (timer.milliseconds() > 250) {
                    intakeState = IntakeState.EXTEND;
                }
                break;
            case EXTEND:
                Bot.intake.extendMisumiDrive();
                Bot.intakeClaw.clawDown();
                intakeState = IntakeState.NONE;
        }
    }

    private void toggleIntakeClaw() {
        intakeClawToggle.toggle(gamepad.b);
        if (intakeClawToggle.justChanged() && intakeClawToggle.get()) {
            Bot.intakeClaw.openClaw();
        } else if (intakeClawToggle.justChanged() && !intakeClawToggle.get()) {
            Bot.intakeClaw.closeClaw();
        }
    }

    private void toggleIntakeWrist() {
        intakeWristToggle.toggle(gamepad.y);
        if (intakeWristToggle.justChanged() && intakeWristToggle.get()) {
            // Down state
            if (Bot.intake.isExtended()) {
                // If intake extended
                Bot.intake.misumiWristDown();
            } else {
                // If intake not extended
                Bot.intake.midMisumiWrist();
            }
        } else if (intakeWristToggle.justChanged() && !intakeWristToggle.get()) {
            // Up state
            if (Bot.intake.isExtended()) {
                // If intake extended
                Bot.intake.midMisumiWrist();
            } else {
                // If intake not extended
                Bot.intake.misumiWristUp();
                Bot.ocgBox.ocgPitchUp();
            }
        }
    }

    private void updateTransferState() {
        intakeTransferToggle.toggle(gamepad.share);
        if (intakeTransferToggle.justChanged() && intakeTransferToggle.get()) {
            transferState = TransferState.WRIST_UP;
        }
    }

    private void handleTransfer() {
        switch (transferState) {
            case NONE:
                break;

            case WRIST_UP:
                Bot.intake.misumiWristUp();
                timer.reset();
                transferState = TransferState.OCG_UP;
                break;

            case OCG_UP:
                if (timer.milliseconds() > 300) {
                    Bot.ocgBox.ocgPitchUp();
                    timer.reset();
                    transferState = TransferState.DROP;
                }
                break;

            case DROP:
                if (timer.milliseconds() > 300) {
                    Bot.intakeClaw.openClaw();
                    timer.reset();
                    transferState = TransferState.OCG_IDLE;
                }
                break;

            case OCG_IDLE:
                if (timer.milliseconds() > 300)
                    transferState = TransferState.WRIST_DOWN;
                break;

            case WRIST_DOWN:
                // As there is nothing after, the state is immediately set to NONE
                Bot.intake.misumiWristDown();
                Bot.intakeClaw.closeClaw();
                Bot.ocgBox.idle();
                transferState = TransferState.NONE;
        }
    }

    public boolean getNorthModeToggle() {
        return northMode;
    }

    public double getSpeed() {
        return speed;
    }
}
