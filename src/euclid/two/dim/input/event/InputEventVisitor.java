package euclid.two.dim.input.event;

public interface InputEventVisitor {
	public void visit(KeyTypedEvent keyDownEvent);

	public void visit(KeyPressedEvent keyPressedEvent);

	public void visit(KeyReleasedEvent keyReleasedEvent);

	public void visit(MouseClickedEvent mouseDownEvent);

	public void visit(MousePressedEvent mousePressedEvent);

	public void visit(MouseReleasedEvent mouseReleasedEvent);

	public void visit(MouseMovedEvent mouseMovedEvent);

	public void visit(MouseDraggedEvent mouseDragedEvent);

}
