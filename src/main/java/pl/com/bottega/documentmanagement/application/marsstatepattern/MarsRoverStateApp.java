package pl.com.bottega.documentmanagement.application.marsstatepattern;

import java.util.Scanner;

/**
 * Created by Dell on 2016-08-28.
 */
public class MarsRoverStateApp {
    public static void main(String[] ars) {
        Scanner scanner = new Scanner(System.in);
        MarsRover marsRover = new MarsRover();
        String command;
        while(true) {
            System.out.println("Rover direction is: " + marsRover.getDirection());
            System.out.println("Rover position is: " + marsRover.position());

            System.out.println("Enter command (m, rl, rr, exit): ");
            command = scanner.next();
            if (command.equals("m"))
                marsRover.move();
            else if (command.equals("rl"))
                marsRover.rotateLeft();
            else if (command.equals("rr"))
                marsRover.rotateRight();
            else if (command.equals("exit"))
                break;
            else
                System.out.println("You have written invalid command");
        }

    }
}
