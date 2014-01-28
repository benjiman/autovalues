package uk.co.benjiweber.autovalues;

import org.junit.Test;
import static org.junit.Assert.*;

public class AutoValueTest {

    interface Person extends ValueType<Person> {
        String name();
        int id();
        public static Person create(String name, int id) {
            return new Person() {
                public String name() { return name; }
                public int id() { return id; }
            }.toValueType();
        }
    }

    @Test public void property_example() {
        Person benji = Person.create("benji", 1);
        assertEquals("benji", benji.name());
        assertEquals(1, benji.id());
    }

    @Test public void equals_example() {
        Person benji1 = Person.create("benji", 1);
        Person benji2 = Person.create("benji", 1);
        Person differentId = Person.create("benji", 2);
        Person differentName = Person.create("benji2", 1);
        Noisy duck = Noisy.create("quack");

        assertEquals(benji1, benji2);
        assertNotEquals(benji1, differentId);
        assertNotEquals(benji1, differentName);
        assertNotEquals(benji1, duck);
        assertNotEquals(benji1, null);
    }

    @Test public void hashcode_example() {
        Person benji1 = Person.create("benji", 1);
        Person benji2 = Person.create("benji", 1);
        Person differentId = Person.create("benji", 2);
        Person differentName = Person.create("benji2", 1);

        assertEquals(benji1.hashCode(), benji2.hashCode());
        assertNotEquals(benji1.hashCode(), differentId.hashCode());
        assertNotEquals(benji1.hashCode(), differentName.hashCode());
    }

    @Test public void toString_example() {
        Person benji = Person.create("benji", 1);

        assertEquals("{[name=benji],[id=1]}", benji.toString());
    }

    interface Noisy extends ValueType<Noisy> {
        String quack();
        public static Noisy create(String noise) {
            return new Noisy() {
                public String quack() {
                    return noise;
                }
            };
        }
    }

}

