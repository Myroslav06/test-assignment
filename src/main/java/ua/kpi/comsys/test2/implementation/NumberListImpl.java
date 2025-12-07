/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ua.kpi.comsys.test2.NumberList;

/**
 * Custom implementation of NumberList interface.
 * Variant: 3317
 * C3 = 2 (Circular Doubly Linked List)
 * C5 = 2 (Octal system - base 8)
 * C7 = 6 (Bitwise OR)
 *
 * @author Alexander Podrubailo (Student ID: 3317)
 */
public class NumberListImpl implements NumberList {

    // Inner class for the doubly linked list node
    private static class Node {
        Byte value;
        Node next;
        Node prev;

        Node(Byte value) {
            this.value = value;
        }
    }

    private Node head;
    private int size;

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                initFromDecimalString(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        initFromDecimalString(value);
    }

    // Helper method to initialize from a decimal string
    // Converts Decimal String -> Octal List
    private void initFromDecimalString(String value) {
        if (value == null || value.isEmpty()) return;
        
        try {
            long decimalValue = Long.parseLong(value);
            if (decimalValue == 0) {
                add((byte) 0);
                return;
            }
            
            // Convert to octal system
            String octalString = Long.toOctalString(decimalValue);
            for (char c : octalString.toCharArray()) {
                byte digit = (byte) Character.getNumericValue(c);
                add(digit);
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + value);
        }
    }

    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(toDecimalString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns student's record book number.
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 3317;
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation (Decimal).
     * Variant requires change to Decimal (C5 + 1) % 5 = 3 -> Decimal.
     */
    public NumberListImpl changeScale() {
        // Current list is in Octal, need to return Decimal.
        // Since this class is designed for Octal (0-7), but we need to store Decimal digits (0-9),
        // we will use unsafeAdd to bypass the range check.

        NumberListImpl decimalList = new NumberListImpl();
        String decimalStr = toDecimalString(); // Convert current number to string "123"

        for (char c : decimalStr.toCharArray()) {
            byte val = (byte) Character.getNumericValue(c);
            decimalList.unsafeAdd(val); // Use method without < 8 check
        }
        return decimalList;
    }

    /**
     * Bitwise OR operation (C7 = 6).
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        NumberListImpl other = (NumberListImpl) arg;
        NumberListImpl result = new NumberListImpl();
        
        if (this.isEmpty() && other.isEmpty()) return result;

        // OR operation for octal numbers is performed bitwise.
        // Aligning to the right (least significant digits).
        
        Node p1 = (this.head != null) ? this.head.prev : null;
        Node p2 = (other.head != null) ? other.head.prev : null;

        int maxLen = Math.max(this.size, other.size);
        
        // Collecting result in a temporary list because we are traversing from the end
        List<Byte> tempResult = new ArrayList<>();

        for (int i = 0; i < maxLen; i++) {
            byte val1 = 0;
            byte val2 = 0;

            if (i < this.size) {
                val1 = p1.value;
                p1 = p1.prev;
            }
            if (i < other.size) {
                val2 = p2.value;
                p2 = p2.prev;
            }

            // Bitwise OR on digits
            byte res = (byte) (val1 | val2);
            tempResult.add(res);
        }

        // Write to result in reverse order (since we collected from the end)
        for (int i = tempResult.size() - 1; i >= 0; i--) {
            result.add(tempResult.get(i));
        }

        return result;
    }

    /**
     * Returns string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        if (size == 0) return "0";
        
        long decimalValue = 0;
        long multiplier = 1;

        // Going from the end (least significant to most significant)
        Node current = head.prev;
        for (int i = 0; i < size; i++) {
            decimalValue += current.value * multiplier;
            multiplier *= 8;
            current = current.prev;
        }
        return String.valueOf(decimalValue);
    }

    @Override
    public String toString() {
        if (head == null) return "";
        StringBuilder sb = new StringBuilder();
        Node current = head;
        do {
            sb.append(current.value);
            current = current.next;
        } while (current != head);
        return sb.toString();
    }

    // --- List methods implementation ---

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (Byte b : this) {
            if (o.equals(b)) return true;
        }
        return false;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;
            private int count = 0;

            @Override
            public boolean hasNext() {
                return count < size;
            }

            @Override
            public Byte next() {
                if (!hasNext()) throw new NoSuchElementException();
                Byte val = current.value;
                current = current.next;
                count++;
                return val;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        int i = 0;
        for (Byte b : this) {
            arr[i++] = b;
        }
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Byte b : this) {
            result[i++] = b;
        }
        if (a.length > size)
            a[size] = null;
        return a;
    }

    // Main add method (for Octal system)
    @Override
    public boolean add(Byte e) {
        if (e < 0 || e > 7) {
             throw new IllegalArgumentException("Octal digit must be 0-7. Got: " + e);
        }
        unsafeAdd(e);
        return true;
    }
    
    // Add method without range check (for changeScale)
    private void unsafeAdd(Byte e) {
        Node newNode = new Node(e);
        if (head == null) {
            head = newNode;
            head.next = head;
            head.prev = head;
        } else {
            Node tail = head.prev;
            tail.next = newNode;
            newNode.prev = tail;
            newNode.next = head;
            head.prev = newNode;
        }
        size++;
    }

    @Override
    public boolean remove(Object o) {
        // Simple implementation: remove first occurrence
        if (head == null) return false;
        Node current = head;
        for (int i = 0; i < size; i++) {
            if (o.equals(current.value)) {
                removeNode(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }
    
    private void removeNode(Node node) {
        if (size == 1) {
            head = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            if (node == head) {
                head = node.next;
            }
        }
        size--;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    @Override
    public Byte get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;
    }

    @Override
    public Byte set(int index, Byte element) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        Byte oldVal = current.value;
        current.value = element;
        return oldVal;
    }

    @Override
    public void add(int index, Byte element) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        if (index == size) {
            add(element);
        } else {
            Node newNode = new Node(element);
            Node current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            // Insert before current
            Node pred = current.prev;
            pred.next = newNode;
            newNode.prev = pred;
            newNode.next = current;
            current.prev = newNode;
            if (index == 0) head = newNode;
            size++;
        }
    }

    @Override
    public Byte remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        Byte val = current.value;
        removeNode(current);
        return val;
    }

    @Override
    public int indexOf(Object o) {
        if (head == null) return -1;
        Node current = head;
        for (int i = 0; i < size; i++) {
            if (o.equals(current.value)) return i;
            current = current.next;
        }
        return -1;
    }

    // --- NumberList methods ---

    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) return false;
        if (index1 == index2) return true;

        Node n1 = head;
        for(int i=0; i<index1; i++) n1 = n1.next;
        
        Node n2 = head;
        for(int i=0; i<index2; i++) n2 = n2.next;

        Byte temp = n1.value;
        n1.value = n2.value;
        n2.value = temp;
        
        return true;
    }

    @Override
    public void sortAscending() {
        if (size <= 1) return;
        // Bubble sort for simplicity with pointers
        for (int i = 0; i < size; i++) {
            Node current = head;
            for (int j = 0; j < size - 1; j++) {
                if (current.value > current.next.value) {
                    Byte temp = current.value;
                    current.value = current.next.value;
                    current.next.value = temp;
                }
                current = current.next;
            }
        }
    }

    @Override
    public void sortDescending() {
        if (size <= 1) return;
        for (int i = 0; i < size; i++) {
            Node current = head;
            for (int j = 0; j < size - 1; j++) {
                if (current.value < current.next.value) {
                    Byte temp = current.value;
                    current.value = current.next.value;
                    current.next.value = temp;
                }
                current = current.next;
            }
        }
    }

    @Override
    public void shiftLeft() {
        if (size <= 1) return;
        head = head.next;
    }

    @Override
    public void shiftRight() {
        if (size <= 1) return;
        head = head.prev;
    }

    // --- Unimplemented or less important methods ---

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte e : c) {
            if (add(e)) modified = true;
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            while (contains(e)) {
                remove(e);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Byte> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Byte> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }
}