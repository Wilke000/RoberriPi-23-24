package org.firstinspires.ftc.teamcode;

import static java.lang.Thread.sleep;

import android.util.Pair;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
public class ArmFunctions {
    private DcMotorEx armMotor, lifter;
    private Servo armRotServo, leftClaw, rightClaw, planeLauncher;

    public ArmFunctions(HardwareMap hardwareMap) {
        armMotor = hardwareMap.get(DcMotorEx.class, "armMotor");
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armRotServo = hardwareMap.get(Servo.class, "armRotServo");
        leftClaw = hardwareMap.get(Servo.class, "leftClaw");
        rightClaw = hardwareMap.get(Servo.class, "rightClaw");
        lifter = hardwareMap.get(DcMotorEx.class, "lifter");
        planeLauncher = hardwareMap.get(Servo.class, "planeLauncher");
    }
    double rightclawPos;
    double leftclawPos;
    double clawRotPos;
    boolean toggleClawRot;
    double servopos;
    double lifterPos;
    int armMotorPos;
    int target;
    double PLpos;

    public void initilize() throws InterruptedException {
        planeLauncher.setPosition(0.4);
        rightClaw.setPosition(0.71);
        leftClaw.setPosition(0);
        Thread.sleep(4000);
        armRotServo.setPosition(0.98);
    }
    public void planeLaunch(boolean togglePlane) {
        if (togglePlane) {
            planeLauncher.setPosition(0.4);
        } else {
            planeLauncher.setPosition(0.8);
        }
    }

    public void armMotor(boolean toggleArm) {
        if (toggleArm) {
            armMotor.setTargetPosition(350);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armMotor.setPower(0.5);
            armRotServo.setPosition(0.7); // up
        } else {
            armMotor.setTargetPosition(20);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armMotor.setPower(0.3);
            armRotServo.setPosition(0.66); // down
        }
    }
    public void fixes(boolean GP2RS, boolean GP2LS) {
        if (GP2RS) {
            armMotor.setTargetPosition(armMotor.getCurrentPosition() - 1);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armMotor.setPower(0.1);
        }
        if (GP2LS) {
            armRotServo.setPosition(armRotServo.getPosition() - 0.001);
        }
    }
    public void lifter(boolean GP1Y, boolean GP1A, boolean GP1B, boolean GP1X) {
        lifterPos = lifter.getCurrentPosition();

        if (GP1Y) {
            lifter.setTargetPosition(0);
            lifter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            lifter.setPower(1);
        } else if (GP1A) {
            lifter.setTargetPosition(7000);
            lifter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            lifter.setPower(1);
        } else if (GP1B) {
            lifter.setTargetPosition(3000);
            lifter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            lifter.setPower(1);
        } else if (GP1X) {
            lifter.setTargetPosition(-7000);
            lifter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            lifter.setPower(0.4);
        }
    }
    public void rightClaw(boolean toggleRightClaw) {
        if (toggleRightClaw) {
            rightClaw.setPosition(0.4); // open
            rightclawPos = rightClaw.getPosition();
        } else {
            rightClaw.setPosition(0.75); // close
            rightclawPos = rightClaw.getPosition();
        }
    }
    public void leftClaw(boolean toggleLeftClaw) {
        if (toggleLeftClaw) {
            leftClaw.setPosition(0); // close
            leftclawPos = leftClaw.getPosition();
        } else {
            leftClaw.setPosition(0.25); // open
            leftclawPos = leftClaw.getPosition();
        }
    }

    public void clawRotation(boolean toggleClawRot) {
        if (toggleClawRot) {
            armRotServo.setPosition(0.66); // down
        } else {
            armRotServo.setPosition(0.98); // up
        }
    }
    /*public void claw(boolean GP2B, boolean isButton2B) {
        if (GP2B) {
            if(!isButton2B) {
                isButton2B = true;
                if (clawServo.getPosition() < 0.15) {
                    clawServo.setPosition(0.27); // close
                } else if (clawServo.getPosition() > 0.15) {
                    clawServo.setPosition(0.13); // open
                }
            }
        } else {
            isButton2B = false;
        }

    }*/
        /*public void clawRot(boolean gamepad2x, boolean gamepad2y) {
        if (gamepad2x) {
            armRotServo.setPosition(0.486); // down
        } else if (gamepad2y) {
            armRotServo.setPosition(0.9); // up
        }
        clawRotPos = armRotServo.getPosition();
    }*/
        /*public Pair<Boolean, Boolean> clawRot(boolean GP2A, boolean lastGP2A, boolean clawRotCheck) {
        if (!lastGP2A && GP2A) {
            if (clawRotCheck) {
                armRotServo.setPosition(0.486); // down
                clawRotCheck = false;
            } else {
                armRotServo.setPosition(0.9); // up
                clawRotCheck = true;
            }
        }
        servopos = armRotServo.getPosition();
        return new Pair<Boolean, Boolean>(clawRotCheck, GP2A);
    }*/
        /*public boolean clawRot(boolean GP2A, boolean lastGP2A, boolean toggleClawRot) {
        if (GP2A != lastGP2A) {
            //toggleClawRot = !toggleClawRot;
            //double clawRotNewPosition = toggleClawRot ? 0.5 : 0.62; // down : up
            //armRotServo.setPosition(clawRotNewPosition);
            lastGP2A = GP2A;
        }
        testing = lastGP2A;
        return lastGP2A;
    }*/
        /*public void leftClaw(boolean GP2X, boolean toggleLeftClaw, boolean lastGP2X) {
        if (GP2X == true && lastGP2X == false) {
            toggleLeftClaw = !toggleLeftClaw;
            double leftClawNewPosition = toggleLeftClaw ? 0.1 : 0.15; // open : close
            leftClaw.setPosition(leftClawNewPosition);
            lastGP2X = GP2X;
        }
    }*/
}