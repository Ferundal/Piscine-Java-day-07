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
        System.out.println("Let's create an object.");
        Constructor [] constructors = currentClass.getClass().getConstructors();
        Constructor constructor = null;
        for (int counter = 0; counter < constructors.length; ++counter) {
            if(constructors[counter].getParameterTypes().length == 0) {
                constructor = constructors[counter];
                break;
            }
        }
        if (constructor == null) {
            throw new RuntimeException("No constructors with parameters");
        }
        Object result = constructor.newInstance();
        Field[] fields = result.getClass().getDeclaredFields();
        Object currentObject = null;
        for (Field field : fields) {
            System.out.println(field.getName() + " (" + field.getType().getSimpleName().toLowerCase() + ")");
            currentObject = readObject(field);
            field.setAccessible(true);
            field.set(result, currentObject);
        }
        return result;
    }

    private static Object readObject(Field field) {
        Object currentObject = null;
        switch (field.getType().getSimpleName().toLowerCase()) {
            case "string":
                currentObject = console.nextLine();
                break;
            case "int":
            case "integer":
                currentObject = console.nextInt();
                console.nextLine();
                break;
            case "long":
                currentObject = console.nextLong();
                console.nextLine();
                break;
            case "double":
                currentObject = console.nextDouble();
                console.nextLine();
                break;
            case "float":
                currentObject = console.nextFloat();
                console.nextLine();
                break;
            case "char":
            case "character":
                currentObject = console.next();
                console.nextLine();
                break;
        }
        return currentObject;
    }

    private static void updateField(Object currentObject) throws IllegalAccessException {
        String nextLine;
        boolean isFieldEntered = false;
        Field[] fields = currentObject.getClass().getDeclaredFields();
        while (!isFieldEntered) {
            System.out.println("Enter name of the field for changing:");
            nextLine = console.nextLine();
            for (Field field : fields) {
                if (field.getName().equals(nextLine)) {
                    Object fieldValue = readObject(field);
                    field.setAccessible(true);
                    field.set(currentObject, fieldValue);
                    isFieldEntered = true;
                }
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
