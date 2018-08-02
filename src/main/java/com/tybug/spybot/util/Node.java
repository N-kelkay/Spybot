package com.tybug.spybot.util;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.MessageEmbed;

public class Node<T>{
	private T data = null;
	private List<Node<T>> children = new ArrayList<>();
	private List<Leaf<T>> leaves = new ArrayList<>();
	private Node<T> parent = null;
	private String unicode;

	public Node(T data) {
		this.data = data;
		this.unicode = "root";
	}

	protected Node(T data, String unicode) {
		this.data = data;
		this.unicode = unicode;
	}

	public void addChild(Node<T> child, String unicode) {
		child.setParent(this);
		child.setName(unicode);
		this.children.add(child);
	}

	public Node<T> addChild(T data, String unicode) {
		Node<T> newChild = new Node<>(data);
		newChild.setName(unicode);
		newChild.setParent(this);
		children.add(newChild);
		return newChild;
	}

	public Leaf<T> addLeaf(int numReacts, String unicode, MessageEmbed queryMessage, MessageEmbed successMessage){
		Leaf<T> newLeaf = new Leaf<>(numReacts, unicode, queryMessage, successMessage);
		leaves.add(newLeaf);
		return newLeaf;
	}


	/**
	 * Names and Children must be the same size
	 * @param children
	 * @param names
	 */
	public void addChildren(List<Node<T>> children, List<String> names) {
		for(int i = 0; i < children.size(); i++) {
			children.get(i).setParent(this);
			children.get(i).setName(names.get(i));
		}
		this.children.addAll(children);
	}

	public List<Node<T>> getChildren() {
		return children;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	private void setParent(Node<T> parent) {
		this.parent = parent;
	}

	public Node<T> getParent() {
		return parent;
	}

	public String getUnicode() {
		return unicode;
	}


	public void setName(String name) {
		this.unicode = name;
	}

	public List<Leaf<T>> getLeaves() {
		return leaves;
	}

	public void setLeaves(List<Leaf<T>> leaves) {
		this.leaves = leaves;
	}

}