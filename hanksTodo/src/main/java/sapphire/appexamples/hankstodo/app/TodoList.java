package sapphire.appexamples.hankstodo.app;

import java.util.ArrayList;

import sapphire.app.*;

public class TodoList implements SapphireObject {
	ArrayList<Object> toDos = new ArrayList<Object>();
	String name = "Hanks todo";

	public TodoList(String name) {
		toDos = new ArrayList<Object>();
		this.name = name;
	}

	public String addToDo(String todo) {
		toDos.add(todo);
		return "OK!";
	}

	public void removeToDo(String value) {
		toDos.remove(value);
		System.out.println("ToDo item removed.");
	}

	public void completeToDo(String todo) {
	}

	public ArrayList<Object> getHighPriority() {
		return new ArrayList<Object>();
	}

	public ArrayList<Object> getAllItems(String name) {
		return toDos;
	}
}
