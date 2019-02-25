package sapphire.appexamples.hankstodo.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import sapphire.app.*;

public class TodoList implements SapphireObject {
    HashMap<String, String> toDos;
    String id = "0";

    public TodoList(String id) {
        toDos = new HashMap<>();
        this.id = id;
    }

    /**
     * Add to do list content with subject and content.
     *
     * @param subject
     * @param content
     * @return
     */
    public String addToDo(String subject, String content) {
        System.out.println("TodoList>> subject: " + subject + " addToDo: " + content);
        String oldContent = toDos.get(subject);
        oldContent = (oldContent == null) ? "" : oldContent + ", ";
        String newContent = oldContent + content;
        toDos.put(subject, newContent);
        return "OK!";
    }

    /**
     * Get to do list content based on subject.
     *
     * @param subject
     * @return
     */
    public String getToDo(String subject) {
        return toDos.get(subject);
    }

    public void completeToDo(String content) {}

	public ArrayList<Object> getHighPriority() {
		return new ArrayList<Object>();
	}

	public void removeToDo(String subject, String content) {
		String oldContent = toDos.get(subject);
        String[] items = oldContent.split(", ");
        List<String> list = new ArrayList<String>(Arrays.asList(items));
        list.remove(content);
        items = list.toArray(new String[0]);
        String newContent = convertStringArrayToString(items, ", ");
		toDos.put(subject, newContent);
		System.out.println("ToDo item removed.");
	}

    private static String convertStringArrayToString(String[] strArr, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr)
            sb.append(str).append(delimiter);
        if(sb.length() >= delimiter.length()) {
            return sb.substring(0, sb.length() - delimiter.length());
        }
        return sb.substring(0, sb.length());
    }
}
