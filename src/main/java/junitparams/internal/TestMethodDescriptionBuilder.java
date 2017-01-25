package junitparams.internal;

import org.junit.runner.Description;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public enum TestMethodDescriptionBuilder {
    REFLECTION_PRIVATE_JUNIT {
        private Constructor<Description> c = null;

        {
            Class<?>[] constructorParamTypes = new Class[] {
                Class.class,
                String.class,
                Serializable.class,
                new Annotation[0].getClass()
            };

            try {
                c = Description.class.getDeclaredConstructor(constructorParamTypes);
                c.setAccessible(true);
            } catch (NoSuchMethodException e) {
                System.err.println("junit has changed, expected " + Description.class.getName() + " private constructor is not there anymore.");
            } catch (SecurityException e) {
                System.err.println("cannot access expected " + Description.class.getName() + " private constructor.");
            }
        }

        @Override
        public Description build(Class<?> aClass, String name, String uniqueMethodId, Annotation[] annotations) {
            if (c != null) {
                try {
                    return c.newInstance(aClass, name, uniqueMethodId, annotations);
                } catch (Exception e) {
                    System.err.println("Cannot build correct " + Description.class.getName() + " object. Fall back to default but incomplete behavior see https://github.com/Pragmatists/JUnitParams/issues/103.");
                }
            }
            return DEFAULT.build(aClass, name, uniqueMethodId, annotations);
        }

    }, DEFAULT {
        @Override
        public Description build(Class<?> aClass, String name, String uniqueMethodId, Annotation[] annotations) {
            return Description.createTestDescription(aClass.getName(), name, uniqueMethodId);
        }
    };

    public abstract Description build(Class<?> aClass, String name, String uniqueMethodId, Annotation[] annotations);
}
