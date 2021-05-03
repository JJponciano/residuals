import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {

	private int[][] startPoint;
	private int[][] endPoint;
	private BufferedImage image;
	private String path;
	private static JFrame frame;
	private static JLabel label;

	public void display() {
		if (frame == null) {
			frame = new JFrame();
			frame.setTitle(path);
			frame.setSize(image.getWidth(), image.getHeight());
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			label = new JLabel();
			label.setIcon(new ImageIcon(image));
			frame.getContentPane().add(label, BorderLayout.CENTER);
			frame.setLocationRelativeTo(null);
			frame.pack();
			frame.setVisible(true);
		} else
			label.setIcon(new ImageIcon(image));
	}

	public Main(int width, int height, String jsonString) {
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.path = "residuals" + UUID.randomUUID() + ".png";

		setTrasnsparancy(width, height);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.red);
		extractJSON(jsonString);
		for (int i = 0; i < endPoint[0].length; i++) {
			int y1 = startPoint[1][i];
			int x1 = startPoint[0][i];
			int x2 = endPoint[0][i];
			int y2 = endPoint[1][i];
			g.drawLine(x1, y1, x2, y2);
		}
	}

	private void setTrasnsparancy(int width, int height) {
		Graphics2D graphics = this.image.createGraphics();

		// To be sure, we use clearRect, which will (unlike fillRect) totally replace
		// the current pixels with the desired color, even if it's fully transparent.
		graphics.setBackground(new Color(0, true));
		graphics.clearRect(0, 0, width, height);
		graphics.dispose();
	}

	public String save() throws IOException {
		ImageIO.write(image, "png", new File(path));
		return path;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	private void extractJSON(String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject coderollsJSONObject = new JSONObject();
			coderollsJSONObject = (JSONObject) parser.parse(jsonString);

			JSONObject geometric_comparison = (JSONObject) coderollsJSONObject.get("geometric_comparison");

			JSONObject member = (JSONObject) geometric_comparison.get("Member");

			// ###########################################################################
			JSONObject residuals = (JSONObject) member.get("residuals");
			// JSONObject residuals = (JSONObject) geometric_comparison.get("residuals");
			// -->TODO Change the structure HERE
			// ###########################################################################

			JSONObject start = (JSONObject) residuals.get("start point");
			JSONArray startX = (JSONArray) start.get("x");
			JSONArray startY = (JSONArray) start.get("y");

			JSONObject end = (JSONObject) residuals.get("end point");
			JSONArray endX = (JSONArray) end.get("x");
			JSONArray endY = (JSONArray) end.get("y");

			this.startPoint = new int[2][startY.size()];
			this.endPoint = new int[2][endX.size()];

			for (int i = 0; i < endX.size(); i++) {
				try {
					startPoint[0][i] = Integer.parseInt(startX.get(i).toString());
					startPoint[1][i] = Integer.parseInt(startY.get(i).toString());
					endPoint[0][i] = Integer.parseInt(endX.get(i).toString());
					endPoint[1][i] = Integer.parseInt(endY.get(i).toString());
				} catch (Exception e) {
					startPoint[0][i] = (int) Double.parseDouble(startX.get(i).toString());
					startPoint[1][i] = (int) Double.parseDouble(startY.get(i).toString());
					endPoint[0][i] = (int) Double.parseDouble(endX.get(i).toString());
					endPoint[1][i] = (int) Double.parseDouble(endY.get(i).toString());
				}

			}

		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
		if (args.length < 3)
			System.out.println("Usage: width height jsonPath \n -d: for displaying");

		int w;
		int h;
		boolean display = false;
		String jsonPath;
		if (args.length == 3) {
			w = Integer.parseInt(args[0]);
			h = Integer.parseInt(args[1]);
			jsonPath = args[2];
		} else {
			display = args[0].contains("-d");
			w = Integer.parseInt(args[1]);
			h = Integer.parseInt(args[2]);
			jsonPath = args[3];
		}

	
			Main main = new Main(w, h, readTextBuffer(jsonPath));
			if (display)
				main.display();
			System.out.println(main.save());

		}  catch (Exception e) {
			System.out.println("Usage: width height jsonPath \n -d: for displaying");
			System.err.println(e.getMessage());
		}

	}

	/**
	 * Reads file with use buffer
	 *
	 * @return the text contained in the file
	 * @throws FileNotFoundException If the file does not exist, is a directory
	 *                               rather than a regular file, or for some other
	 *                               reason cannot be opened for reading.
	 * @throws IOException           if something wrong.
	 */
	private static String readTextBuffer(String path) throws FileNotFoundException, IOException {
		final StringBuilder buff = new StringBuilder();
		final File fileio = new File(path);

		BufferedReader reader = Files.newBufferedReader(fileio.toPath(), StandardCharsets.UTF_8);
		String line;
		while ((line = reader.readLine()) != null) {
			buff.append(line).append("\n");
		}
		String buffS = buff.toString();
		buffS = buffS.replaceFirst("[" + "\\s" + "]*" + "$", "");
		return buffS;

	}

}
