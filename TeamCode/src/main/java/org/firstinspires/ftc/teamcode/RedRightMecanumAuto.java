package org.firstinspires.ftc.teamcode;

import android.util.Size;

import androidx.annotation.NonNull;

// RR-specific imports
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

// Non-RR imports
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.opencv.core.Scalar;


@Config
@Autonomous(name = "RedCloseAuto", group = "Autonomous")
public class RedRightMecanumAuto extends LinearOpMode {
    int visionOutputPos = 3;
    public class ArmMotor {
        private DcMotorEx armMotor;
        private Servo armRotServo;

        public ArmMotor(HardwareMap hardwareMap) {
            armMotor = hardwareMap.get(DcMotorEx.class, "armMotor");
            armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            armRotServo = hardwareMap.get(Servo.class, "armRotServo");
        }
        public class ArmUp implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                armMotor.setTargetPosition(350);
                armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                armMotor.setPower(0.5);
                armRotServo.setPosition(0.7); // up
                return false;
            }
        }
        public Action armUp() {
            return new ArmUp();
        }

        public class ArmDown implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                armMotor.setTargetPosition(20);
                armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                armMotor.setPower(0.3);
                return false;
            }
        }
        public Action armDown() {
            return new ArmDown();
        }
    }

    public class LeftClaw {
        private Servo leftClaw;

        public LeftClaw(HardwareMap hardwareMap) {
            leftClaw = hardwareMap.get(Servo.class, "leftClaw");
        }
        public class CloseLeft implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                leftClaw.setPosition(0); // CHANGE THIS (THIS IS CLOSED LEFT)
                return false;
            }
        }
        public Action closeLeft() {
            return new CloseLeft();
        }
        public class OpenLeft implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                leftClaw.setPosition(0.4); // CHANGE THIS (THIS IS OPEN LEFT)
                return false;
            }
        }
        public Action openLeft() {
            return new OpenLeft();
        }
    }
    public class RightClaw {
        private Servo rightClaw;

        public RightClaw(HardwareMap hardwareMap) {
            rightClaw = hardwareMap.get(Servo.class, "rightClaw");
        }
        public class CloseRight implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                rightClaw.setPosition(0.75); // CHANGE THIS (THIS IS CLOSED RIGHT)
                return false;
            }
        }
        public Action closeRight() {
            return new CloseRight();
        }
        public class OpenRight implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                rightClaw.setPosition(0.4); // CHANGE THIS (THIS IS OPEN RIGHT)
                return false;
            }
        }
        public Action openRight() {
            return new OpenRight();
        }
    }
    public class ClawRot {
        private Servo armRotServo;

        public ClawRot(HardwareMap hardwareMap) {
            armRotServo = hardwareMap.get(Servo.class, "armRotServo");
        }
        public class RotClawUp implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                armRotServo.setPosition(0.98);
                return false;
            }
        }
        public Action rotClawUp() {
            return new RotClawUp();
        }
        public class RotClawDown implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                armRotServo.setPosition(0.66);
                return false;
            }
        }
        public Action rotClawDown() {
            return new RotClawDown();
        }
    }

    public ColourMassDetectionProcessor colourMassDetectionProcessor;
    public VisionPortal visionPortal;

    @Override
    public void runOpMode() throws InterruptedException {
        VisionPortal visionPortal;
        // HSV takes the form: (HUE, SATURATION, VALUE)
        Scalar lower = new Scalar(0, 100, 100); // the lower hsv
        Scalar upper = new Scalar(60, 255, 255); // the upper hsv
        double minArea = 10000;

        colourMassDetectionProcessor = new ColourMassDetectionProcessor(
                    lower,
                    upper,
                    () -> minArea,
                    () -> 500, // the left dividing line
                    () -> 1000 // the left dividing line
        );
        visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                    .addProcessor(colourMassDetectionProcessor)
                    .setCameraResolution(new Size(1280, 720))
                    .build();


        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(11, -60, Math.toRadians(90)));
        LeftClaw leftClaw = new LeftClaw(hardwareMap);
        RightClaw rightClaw = new RightClaw(hardwareMap);
        ClawRot clawRot = new ClawRot(hardwareMap);
        ArmMotor arm = new ArmMotor(hardwareMap);
        Action trajectoryAction1;
        Action trajectoryAction2;
        Action trajectoryAction3;
        Action trajectoryActionCloseOut;
        trajectoryAction1 = drive.actionBuilder(drive.pose)
                .strafeTo(new Vector2d(14, -35.5))
                .turnTo(Math.toRadians(180))
                .waitSeconds(1) // open right
                .stopAndAdd(clawRot.rotClawDown())
                .waitSeconds(0.5)
                .stopAndAdd(rightClaw.openRight())
                .strafeTo(new Vector2d(20, -34))
                .stopAndAdd(rightClaw.closeRight())
                .stopAndAdd(clawRot.rotClawUp())
                .turnTo(Math.toRadians(0))
                .splineToSplineHeading(new Pose2d(38, -30, 0), 0)
                .stopAndAdd(arm.armUp())
                .waitSeconds(1) // arm up
                .lineToX(49.5)
                .waitSeconds(1) // open left
                .stopAndAdd(leftClaw.openLeft())
                .waitSeconds(0.5)
                .lineToX(40)
                .build();
        trajectoryAction2 = drive.actionBuilder(drive.pose)
                .setTangent(0)
                .strafeTo(new Vector2d(29, -25))
                .waitSeconds(0.5)
                .turnTo(Math.toRadians(180))
                .stopAndAdd(clawRot.rotClawDown())
                .waitSeconds(0.5) // open left
                .stopAndAdd(rightClaw.openRight())
                .waitSeconds(0.5)
                .stopAndAdd(clawRot.rotClawUp())
                .stopAndAdd(rightClaw.closeRight())
                .turnTo(Math.toRadians(0))
                .splineToLinearHeading(new Pose2d(44.5, -35, 0), 1)
                .stopAndAdd(arm.armUp())
                .waitSeconds(1) // arm up
                .lineToX(49.5)
                .waitSeconds(0.5) // open left
                .stopAndAdd(leftClaw.openLeft())
                .waitSeconds(0.5)
                .lineToX(40)
                .build();
        trajectoryAction3 = drive.actionBuilder(drive.pose)
                .strafeTo(new Vector2d(21, -46))
                .waitSeconds(0.5) // open left
                .stopAndAdd(clawRot.rotClawDown())
                .waitSeconds(0.5)
                .stopAndAdd(rightClaw.openRight())
                .waitSeconds(1)
                .stopAndAdd(clawRot.rotClawUp())
                .stopAndAdd(rightClaw.closeRight())
                .strafeTo(new Vector2d(38, -39))
                .turnTo(Math.toRadians(0))
                .waitSeconds(1) // arm up
                .stopAndAdd(arm.armUp())
                .waitSeconds(0.5)
                .lineToX(50)
                .waitSeconds(1) // open left
                .stopAndAdd(leftClaw.openLeft())
                .waitSeconds(0.5)
                .lineToX(40)
                .stopAndAdd(leftClaw.closeLeft())
                .build();
        trajectoryActionCloseOut = drive.actionBuilder(drive.pose)
                .strafeTo(new Vector2d(46, -58))
                .turnTo(Math.toRadians(0))
                .stopAndAdd(leftClaw.closeLeft())
                .stopAndAdd(rightClaw.closeRight())
                .stopAndAdd(arm.armDown())
                .waitSeconds(1)
                .stopAndAdd(clawRot.rotClawUp())
                .strafeTo(new Vector2d(50, -58))
                .waitSeconds(15)
                .build();


        Actions.runBlocking(leftClaw.closeLeft());
        Actions.runBlocking(rightClaw.closeRight());
        Thread.sleep(5000);
        Actions.runBlocking(clawRot.rotClawUp());

        while (!isStopRequested() && !opModeIsActive()) {
            ColourMassDetectionProcessor.PropPositions recordedPropPosition = colourMassDetectionProcessor.getRecordedPropPosition();
            if (recordedPropPosition == ColourMassDetectionProcessor.PropPositions.UNFOUND) {
                recordedPropPosition = ColourMassDetectionProcessor.PropPositions.RIGHT;
            }
            telemetry.addData("Position during Init", recordedPropPosition);
            telemetry.update();
        }
        ColourMassDetectionProcessor.PropPositions recordedPropPosition = colourMassDetectionProcessor.getRecordedPropPosition();
        waitForStart();
        if (isStopRequested()) return;
        // gets the recorded prop position
        telemetry.addData("Final detected pos", recordedPropPosition);
        telemetry.update();
        // now we can use recordedPropPosition to determine where the prop is! if we never saw a prop, your recorded position will be UNFOUND.
        // if it is UNFOUND, you can manually set it to any of the other positions to guess

        Action trajectoryActionChosen = trajectoryAction3;
        // now we can use recordedPropPosition in our auto code to modify where we place the purple and yellow pixels
        switch (recordedPropPosition) {
            case LEFT:
                trajectoryActionChosen = trajectoryAction1;
                break;
            case UNFOUND:// we can also just add the unfound case here to do fallthrough intstead of the overriding method above, whatever you prefer!
                visionOutputPos = 3;
                trajectoryActionChosen = trajectoryAction3;
                break;
            case MIDDLE:
                trajectoryActionChosen = trajectoryAction2;
                visionOutputPos = 2;
                break;
            case RIGHT:
                trajectoryActionChosen = trajectoryAction3;
                visionOutputPos = 3;
                break;
                // code to do if we saw the prop on the right
        }
        Actions.runBlocking(
                new SequentialAction(
                        trajectoryActionChosen,
                        trajectoryActionCloseOut
                )
        );

    }
}