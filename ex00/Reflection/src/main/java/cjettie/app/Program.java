package cjettie.app;

import cjettie.users.User;
import cjettie.vehicle.Car;

import java.lang.reflect.*;
import java.util.*;

public class Program {
    public static Scanner console = new Scanner(System.in);

    public static void main(String [] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object [] classes = new Object[2];
        classes[0] = new User();
        classes[1] = new Car();
        while (true) {
            showClasses(classes);
            System.out.println("---------------------");
            String nextLine;
            Object currentClass = null;
            while (currentClass == null) {
                System.out.println("Enter class name:");
                nextLine = console.nextLine();
                for (int classIndex = 0; classIndex < classes.length; ++classIndex) {
                    if (classes[classIndex].getClass().getSimpleName().equals(nextLine)) {
                        currentClass = classes[classIndex];
                        break;
                    }
                }
            }
            System.out.println("---------------------");
            showClassDetails(currentClass);
            System.out.println("---------------------");
            Object instance = createNewObject(currentClass);
            System.out.println("Object created: " + instance.toString());
            System.out.println("---------------------");
            updateField(instance);
            System.out.println("Object updated: " + instance.toString());
            System.out.println("---------------------");
        }
    }

    private static Object createNewObject( Object currentClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor [] constructors = currentClass.getClass().getConstructors();
        Parameter[] parameters = constructors[constructors.length - 1].getParameters();
        List<Object>constructorInputParameters = new ArrayList<Object>();
        for (int counter = 0; counter < parameters.length; ++counter) {
            System.out.println(parameters[counter].getName());
            Object currentObject = new Object();
            switch (parameters[counter].getType().getSimpleName().toLowerCase()) {
                case "string":
                    currentObject = console.nextLine();
                    break;
                case "int":
                case "integer":
                    currentObject = console.nextInt();
                    break;
                case "long":
                    currentObject = console.nextLong();
                    break;
                case "double":
                    currentObject = console.nextDouble();
                    break;
                case "float":
                    currentObject = console.nextFloat();
                    break;
                case "char":
                case "character":
                    currentObject = console.next();
                    break;
            }
            constructorInputParameters.add(currentObject);
        }
        return constructors[constructors.length - 1].newInstance(constructorInputParameters.toArray());
    }

    private static Object readObject(Class classCurrent) {
        switch (classCurrent.getSimpleName().toLowerCase()) {
            case "string":
                return console.nextLine();
            case "int":
            case "integer":
                return Integer.getInteger(console.nextLine());
            case "long":
                return Long.getLong(console.nextLine());
            case "double":
                return Double.parseDouble(console.nextLine());
            case "float":
                return Float.parseFloat(console.nextLine());
            case "char":
            case "character":
                return console.next();
        }
        return null;
    }

    private static void updateField(Object currentObject) throws IllegalAccessException {
        System.out.println("Enter name of the field for changing:");
        String nextLine = console.nextLine();
        nextLine = console.nextLine();
        for (int counter = 0; counter <  currentObject.getClass().getDeclaredFields().length; ++counter) {
            if (currentObject.getClass().getDeclaredFields()[counter].getName().equals(nextLine)) {
                System.out.println("Enter " + currentObject.getClass().getDeclaredFields()[counter].getType().getSimpleName() + " value:");
                Object tempObject = readObject(currentObject.getClass());
                currentObject.getClass().getDeclaredFields()[counter].setAccessible(true);
                currentObject.getClass().getDeclaredFields()[counter]
                        .set(currentObject.getClass().getDeclaredFields()[counter].getType(),
                                tempObject);
            }
        }
    }
    private static void showClassDetails(Object currentClass) {
        System.out.println("fields:");
        Field[] fields = currentClass.getClass().getDeclaredFields();
        for (int counter = 0; counter < fields.length; ++counter) {
            System.out.println("\t" + fields[counter].getType().getSimpleName()  + " " + fields[counter].getName());
        }
        System.out.println("methods:");
        Method[] methods = currentClass.getClass().getDeclaredMethods();
        for (int counter = 0; counter < methods.length; ++counter) {
            System.out.print("\t" + methods[counter].getReturnType().getSimpleName() + " "
                    + methods[counter].getName() + " (" );
            for (int methodCounter = 0; methodCounter < methods[counter].getParameterCount(); ++methodCounter) {
                System.out.print(methods[counter].getParameters()[methodCounter].getType().getSimpleName());
                if (methodCounter != methods[counter].getParameters().length - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println(")");
        }
    }
    private static void showClasses(Object [] objects) {
        System.out.println("Classes:");
        for (int counter = 0; counter < objects.length; ++counter) {
            System.out.println(objects[counter].getClass().getSimpleName());
        }
    }
}
