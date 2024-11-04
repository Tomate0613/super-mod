package dev.doublekekse.super_mod;

import java.util.Arrays;

public class CircularBuffer<T> {
    private final T[] buffer;        // Array to store the elements
    private int head;                // Index of the next element to overwrite
    private int count;               // Current number of elements in the buffer

    public CircularBuffer(int size) {
        if (size <= 0) throw new IllegalArgumentException("Size must be greater than zero");
        this.buffer = (T[]) new Object[size]; // Create a generic array
        this.head = 0;
        this.count = 0;
    }

    // Adds an element to the buffer
    public int add(T element) {
        buffer[head] = element;       // Add the new element
        head = (head + 1) % buffer.length; // Move head to the next index
        if (count < buffer.length) {
            count++;                  // Increase count until buffer is full
        }

        return count - 1;
    }

    // Returns the elements in the buffer as an array
    public T[] toArray() {
        T[] result = (T[]) new Object[count];
        for (int i = 0; i < count; i++) {
            result[i] = buffer[(head - count + i + buffer.length) % buffer.length];
        }
        return result;
    }

    // Returns the current number of elements in the buffer
    public int size() {
        return count;
    }

    public int limit() {
        return buffer.length;
    }

    // Checks if the buffer is empty
    public boolean isEmpty() {
        return count == 0;
    }

    // Clears the buffer
    public void clear() {
        Arrays.fill(buffer, null);
        head = 0;
        count = 0;
    }

    public CircularBuffer<T> clone() {
        CircularBuffer<T> clonedBuffer = new CircularBuffer<>(buffer.length);
        clonedBuffer.head = this.head;
        clonedBuffer.count = this.count;

        // Copy elements to the cloned buffer
        for (int i = 0; i < this.count; i++) {
            clonedBuffer.buffer[i] = this.buffer[(this.head - this.count + i + this.buffer.length) % this.buffer.length];
        }
        return clonedBuffer;
    }

    // Get method to retrieve element at specified index
    public T get(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return buffer[(head - count + index + buffer.length) % buffer.length];
    }
}
