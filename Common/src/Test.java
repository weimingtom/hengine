import java.nio.ByteBuffer;
import java.util.UUID;

import com.hvilela.common.Position;
import com.hvilela.common.messages.MovementMessage;

public class Test {
	public static void main(String[] args) {
		UUID id = UUID.randomUUID();

		float x = (float) (Math.random() * 20);
		float y = (float) (Math.random() * 20);
		float z = (float) (Math.random() * 20);
		float rotation = (float) (Math.random() * 360);
		Position position = new Position(x, y, z, rotation);

		float speed = (float) (Math.random() * 20);
		MovementMessage message1 = new MovementMessage(id, position, speed);
		System.out.println(message1);
		
		ByteBuffer bytes = message1.toBytes();
		bytes.arrayOffset();
		bytes.position(0);

		MovementMessage message2 = new MovementMessage();
		message2.read(bytes);
		System.out.println(message2);

		System.out.println();
	}
}