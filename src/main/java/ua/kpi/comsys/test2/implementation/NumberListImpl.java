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
 * <p>
 * This implementation is based on the student variant <b>3317</b>:
 * <ul>
 * <li><b>C3 = 2</b>: Circular Doubly Linked List</li>
 * <li><b>C5 = 2</b>: Octal system (Base-8, digits 0-7)</li>
 * <li><b>C7 = 6</b>: Bitwise OR operation</li>
 * </ul>
 *
 * @author Alexander Podrubailo (Student ID: 3317)
 * @version 1.0
 */
public class NumberListImpl implements NumberList {

    /**
     * Inner class representing a node in the doubly linked list.
     */
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
     * Default constructor.
     * <p>
     * Creates an empty {@code NumberListImpl}.
     */
    public NumberListImpl() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Constructs a new {@code NumberListImpl} using a <b>decimal</b> number
     * read from a file.
     *
     * @param file the file containing the number string.
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
     * Constructs a new {@code NumberListImpl} using a <b>decimal</b> number
     * string representation.
     *
     * @param value the number in string notation (decimal).
     */
    public NumberListImpl(String value) {
        this();
        initFromDecimalString(value);
    }

    /**
     * Helper method to initialize the list from a decimal string.
     * Converts the Decimal String to an Octal List representation.
     *
     * @param value the decimal number string.
     */
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
     * Saves the number stored in the list into the specified file
     * in <b>decimal</b> notation.
     *
     * @param file the file where the number has to be stored.
     */
    public void saveList(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(toDecimalString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the student's record book number used to determine the variant.
     *
     * @return the student's record book number (3317).
     */
    public static int getRecordBookNumber() {
        return 3317;
    }

    /**
     * Returns a new {@code NumberListImpl} which represents the same number
     * in the Decimal scale of notation.
     * <p>
     * Calculated as: (C5 + 1) % 5 = 3 (Decimal).
     *
     * @return a new {@code NumberListImpl} containing decimal digits.
     */
    public NumberListImpl changeScale() {
        // Current list is in Octal, need to return Decimal.
        // We use unsafeAdd to bypass the 0-7 range check for decimal digits (0-9).

        NumberListImpl decimalList = new NumberListImpl();
        String decimalStr = toDecimalString(); 

        for (char c : decimalStr.toCharArray()) {
            byte val = (byte) Character.getNumericValue(c);
            decimalList.unsafeAdd(val); 
        }
        return decimalList;
    }

    /**
     * Performs the Bitwise OR operation (C7 = 6).
     * <p>
     * The operation is performed bitwise on the octal digits of this list and the argument list.
     * Alignment is performed on the least significant digits.
     *
     * @param arg the second operand of the operation.
     * @return a new {@code NumberListImpl} representing the result of the OR operation.
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        NumberListImpl other = (NumberListImpl) arg;
        NumberListImpl result = new NumberListImpl();
        
        if (this.isEmpty() && other.isEmpty()) return result;

        Node p1 = (this.head != null) ? this.head.prev : null;
        Node p2 = (other.head != null) ? other.head.prev : null;

        int maxLen = Math.max(this.size, other.size);
        
        // Use a temporary list to collect digits because we traverse from the end
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

        // Add to result in reverse order
        for (int i = tempResult.size() - 1; i >= 0; i--) {
            result.add(tempResult.get(i));
        }

        return result;
    }

    /**
     * Returns the string representation of the number stored in the list
     * converted to the <b>decimal</b> scale of notation.
     *
     * @return the string representation in decimal scale.
     */
    public String toDecimalString() {
        if (size == 0) return "0";
        
        long decimalValue = 0;
        long multiplier = 1;

        // Traverse from the end (least significant to most significant)
        Node current = head.prev;
        for (int i = 0; i < size; i++) {
            decimalValue += current.value * multiplier;
            multiplier *= 8;
            current = current.prev;
        }
        return String.valueOf(decimalValue);
    }

    /**
     * Returns the string representation of the number in its current (Octal) notation.
     *
     * @return the string representation of the list elements.
     */
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

    @SuppressWarnings("unchecked")
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

    /**
     * Appends the specified element to the end of this list.
     * Ensures the element is a valid Octal digit (0-7).
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     * @throws IllegalArgumentException if the element is not between 0 and 7.
     */
    @Override
    public boolean add(Byte e) {
        if (e < 0 || e > 7) {
             throw new IllegalArgumentException("Octal digit must be 0-7. Got: " + e);
        }
        unsafeAdd(e);
        return true;
    }
    
    /**
     * Internal add method without range check.
     * Used for building Decimal lists in changeScale().
     *
     * @param e the byte to add.
     */
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
    
    /**
     * Helper to unlink a node from the circular list.
     */
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

    // --- NumberList specific methods ---

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void sortAscending() {
        if (size <= 1) return;
        // Bubble sort for simplicity
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void shiftLeft() {
        if (size <= 1) return;
        head = head.next;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shiftRight() {
        if (size <= 1) return;
        head = head.prev;
    }

    // --- Unimplemented methods (optional for this assignment) ---

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