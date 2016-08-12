package com.epam.java.rt.rater.model.reflective;

/**
 * rater
 */
public class ReflectiveStack<StackItem> {
    private ReflectiveNode next;
    private int size;

    public ReflectiveStack() {
        this.next = null;
        this.size = 0;
    }

    public int push(StackItem stackItem) {
        this.next = new ReflectiveNode(stackItem, this.next);
        return this.size++;
    }

    public StackItem pop() {
        StackItem stackItem = this.next.stackItem;
        this.next = this.next.next;
        this.size--;
        return stackItem;
    }

    public class ReflectiveNode {
        private final StackItem stackItem;
        private final ReflectiveNode next;

        ReflectiveNode(StackItem stackItem, ReflectiveNode reflectiveNode) {
            this.stackItem = stackItem;
            this.next = reflectiveNode;
        }

        public StackItem getStackItem() {
            return this.stackItem;
        }
    }
}