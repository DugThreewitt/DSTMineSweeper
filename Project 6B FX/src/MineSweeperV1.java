import java.util.*;

//import javafx.*;
//import javafx.scene.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Stage;

/* *****************************************************************************
* Name    : MineSweeper.java class 
* Purpose : This is class play the game MineSweeper
* Inputs  : 
* Outputs : 
* *****************************************************************************/

public class MineSweeperV1 extends Application
{

	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		// constant chars for mine and ?
		final char MINE = 'M', EMPTY = ' ';
		boolean [][] hasBombs = new boolean [8][8];
		
		
		Pane gameContainer = new Pane(); //to contain the grid so I can constrain to a square
		GridPane gameGrid = new GridPane(); //to create 8x8 grid for game
		HBox showAndStart = new HBox(20); // to hold Show and Start buttons
		VBox appContainer = new VBox();
		HBox insContainer = new HBox();
		VBox lost = new VBox();
		insContainer.setPadding(new Insets (20, 20, 20, 20));
		
		//Stage inst = new Stage();
		Stage lose = new Stage();
		
		//inst.setX(0);
		//inst.setY(0);
		Image image = new Image("bomb.jpg");
		ImageView nuke = new ImageView(image);
		StackPane imgPane = new StackPane();
		imgPane.getChildren().add(nuke);
		imgPane.setPadding(new Insets(20, 20, 20, 20));
		
		
		//Create Scene for game
		Scene scene = new Scene(appContainer, 800, 900);
		//Scene instructions = new Scene(insContainer, 500, 150);
		Scene youLost = new Scene(lost, 1000, 600);

		
		Text ins1 = new Text("To play: \nLeft Click to reveal what's beneath the buttons \n"
				+ "Right Click to tag a mine \n"
				+ "(be careful, if it's not a mine, the game is over and you lose!) \n"
				+ "Tag all 10 mines and you win!");
		
		Text youLose = new Text("  Boom, you're dead...");
		youLose.setFont(Font.font("Gill Sans", 100));
		youLose.setFill(Color.RED);
		youLose.setTextAlignment(TextAlignment.CENTER);
		
		
		
		insContainer.getChildren().add(ins1);
		lost.getChildren().addAll(youLose, imgPane);

		
		// Set preferred Size for show/start and gameContainer
		showAndStart.setPrefSize(800, 50);
		gameContainer.setPrefSize(800, 800);
		
		// Create buttons
		Button btShow = new Button("Show");
		Button btStart = new Button("Start");
		
		// cell to hold buttons
		Cell [][] gameCell = new Cell [8][8];
		
		//buttons for gameplay
		GameButton [][] btGame = new GameButton[8][8];
		
		Text [][] cellText = new Text[8][8];
		
		//add panes 
		appContainer.getChildren().addAll(showAndStart, gameContainer, insContainer);
		gameContainer.getChildren().addAll(gameGrid);
		showAndStart.getChildren().addAll(btStart, btShow);
		
		//bind and align panes
		gameContainer.prefHeightProperty().bind(appContainer.heightProperty().subtract(50));
		gameContainer.setStyle("-fx-background-color: darkgray;");
		
		gameGrid.prefWidthProperty().bind(gameContainer.widthProperty().subtract(10));
		gameGrid.prefHeightProperty().bind(gameContainer.heightProperty().subtract(10));
		gameGrid.setPadding(new Insets(10,0,0,10));
		
		showAndStart.setAlignment(Pos.CENTER);
		
		
		// add cells and buttons to grid pane
		for(int i = 0; i < gameCell.length; i++)
			for(int j = 0; j < gameCell[i].length; j++)
			{
				gameGrid.add(gameCell[i][j] = new Cell(EMPTY), j, i);
				gameCell[i][j].getChildren().add(cellText[i][j] = new Text(""));
				gameCell[i][j].getChildren().add(btGame[i][j] = new GameButton(i, j));
				gameCell[i][j].prefWidthProperty().bind(gameContainer.widthProperty().divide(8.0));
				gameCell[i][j].prefHeightProperty().bind(gameContainer.heightProperty().divide(8.0));
				btGame[i][j].prefWidthProperty().bind(gameContainer.widthProperty().divide(8.0));
				btGame[i][j].prefHeightProperty().bind(gameContainer.heightProperty().divide(8.0));
				Cell [][] passCell = gameCell; // had to create to pass, it wanted gameCell to be constant
				btGame[i][j].setOnMouseClicked(new EventHandler<MouseEvent>()
						{
							public void handle(MouseEvent event)
							{
								GameButton clickedButton = (GameButton)event.getSource();
								if (((MouseEvent) event).getButton().equals(MouseButton.SECONDARY))
									{
										if(clickedButton.isBomb())
										{
											clickedButton.setText("M");
											clickedButton.setFont(Font.font("Gill Sans", 25));
											clickedButton.setTextFill(Color.RED);
										}
										else
										{
											//System.out.println("You chose poorly! Game Over.");
											GameButton [][] b = btGame;
											b = showAll(btGame);
											
											lose.setTitle("You Lost");
											lose.setScene(youLost);
											lose.show();
										}
									}
								if(((MouseEvent) event).getButton().equals(MouseButton.PRIMARY))
									{
										if(clickedButton.isBomb())
										{
											GameButton [][] b = btGame;
											b = showAll(btGame);
											
											lose.setTitle("You Lost");
											lose.setScene(youLost);
											lose.show();
										}
										else
										{
											expand(btGame, clickedButton);
										}
									}
							}
						});
				
			}
		
		hasBombs = setBombs(hasBombs);
		gameCell = placeBombs(hasBombs, btGame, gameCell, cellText);
		//findNeighbors(gameCell, cellText, 0, 0);
		
		Cell c [][] = gameCell; // had to create to pass, eclipse wanted gameCell to be constant
		boolean b [][] = hasBombs; // had to create to pass, eclipse wanted hasBombs to be constant
		
		btShow.setOnMouseClicked(e -> /*handleButtonClick(btGame)*/handleShowClick(btGame, c));
		btStart.setOnMouseClicked(e -> handleStartClick(b, btGame, c, cellText));
		
		/*
			create Cell that extends stackpane - done
			put btGame in the cell - done
			put numbers and bombs in cell
				place bombs - Done
				if hasbomb set token to MINE
					recursive function to see if hasBomb set token to sum of bombs - done
			onclick make btGame setvisible false
				recursive function if empty check surrounding boxes for numbers-setvisible false
			show onclick 
				if hasBomb btGame.setVisible(false) - Done
			start onclick
				reset scene
				place bombs - Done
			
		
		*/
		// TODO Auto-generated method stub
		
		primaryStage.setTitle("Minesweeper v1b");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		//inst.setTitle("Instructions");;
		//inst.setScene(instructions);
		//inst.show();
		
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	/* *****************************************************************************
	* Name    : Cell class 
	* Purpose : Creates a cell to hold the text and buttons
	* Inputs  : 
	* Outputs : 
	* *****************************************************************************/

	private class Cell extends StackPane
	{
		private char token = ' ';
		
		public Cell ()
		{
			setStyle("-fx-background-color: white; -fx-border-color: lightgray");
		}
		
		public Cell (char c)
		{
			setStyle("-fx-background-color: white; -fx-border-color: lightgray");
			this.token = c;
		}
		
	    public char getToken() 
	    {
	        return token;
	    }
	    
	    public void setToken(char c)
	    {
	    	token = c;
	    }
	}
	
	/* *****************************************************************************
	* Name    : GameButton class 
	* Purpose : Creates a GameButton that holds it's position in the array
	* Inputs  : 
	* Outputs : 
	* *****************************************************************************/

	private class GameButton extends Button	
	{
		private int xPos, yPos;
		private boolean isBomb = false;
		
		public GameButton ()
		{
			
		}
		
		public GameButton (int xPos, int yPos)
		{
			this.xPos = xPos;
			this.yPos = yPos;
		}
		
	    public int getXPos() 
	    {
	        return xPos;
	    }
	    
	    public int getYPos()
	    {
	    	return yPos;
	    }
	    
	    public void setXPos(int xPos)
	    {
	    	this.xPos = xPos;
	    }
	    public void setYPos(int yPos)
	    {
	    	this.yPos = yPos;
	    }
	    
	    public void setBomb(boolean isBomb)
	    {
	    	this.isBomb = isBomb;
	    }
	    
	    public boolean isBomb()
	    {
	    	return isBomb;
	    }
	    
	}
	
	/* *****************************************************************************
	* Name    : setBombs 
	* Purpose : randomly puts 10 bombs in a 2D array
	* Inputs  : 2D boolean array hasBombs
	* Outputs : 2D boolean array 
	* *****************************************************************************/
	
	boolean [][] setBombs(boolean [][] hasBombs)
	{
		boolean [][] b = hasBombs;
		
		int x = b.length;
		int y = b[0].length;
		
		boolean [] temp = new boolean[x * y];
		
		int totalBombs = 10;
		
		//set temp to all false
		for(int i = 0; i < temp.length; i++)
		{
			temp[i] = false;
		}
		
		for(int i = 0; i < b.length; i++)
			for(int j = 0; j < b[0].length; j++)
			{
				b[i][j] = false;
			}
		
		// randomly fill temp with trues
		while(totalBombs > 0)
		{
			int rand = (int)(Math.random() * temp.length);
			
			if(temp[rand] == false)
			{
				temp[rand] = true;
				totalBombs --;
			}
		}
		
		// move true from 1d to 2d array
		for(int i = 0; i < temp.length; i++)
		{
			if(temp[i] == true)
			{
				int bx = (int)i / x;
				int by = i % x;
				b[bx][by] = true;
			}
		}
		
		return b;
	}
	
	/* *****************************************************************************
	* Name    : showAll 
	* Purpose : sets all buttons visibility to false
	* Inputs  : 2D button array 
	* Outputs : 2D button array 
	* *****************************************************************************/
		
	private GameButton [][] showAll(GameButton [][] bt)
	{
		for(int i = 0; i < bt.length; i++)
			for(int j = 0; j < bt[0].length; j++)
			{
				bt[i][j].setVisible(false);
			}
		
		return bt;
		
	}
	
	/* *****************************************************************************
	* Name    : showAll 
	* Purpose : sets all buttons visibility to false
	* Inputs  : 2D button array 
	* Outputs : 2D button array 
	* *****************************************************************************/
		
	private GameButton [][] showNone(GameButton [][] bt)
	{
		for(int i = 0; i < bt.length; i++)
			for(int j = 0; j < bt[0].length; j++)
			{
				bt[i][j].setVisible(true);
			}
		
		return bt;
		
	}
	
	/* *****************************************************************************
	* Name    : showBombs 
	* Purpose : set visibility of buttons above bombs to false
	* Inputs  : 2D button array and 2D cell array
	* Outputs : 2D button array 
	* *****************************************************************************/
	
	private GameButton [][] showBombs(GameButton [][] bt, Cell [][] c)
	{
		for(int i = 0; i < bt.length; i++)
			for(int j = 0; j < bt[0].length; j++)
			{
				if(c[i][j].getToken() == 'M')
				{
					bt[i][j].setVisible(false);
				}
			}
		
		return bt;
		
	}
	
	/* *****************************************************************************
	* Name    : placeBombs 
	* Purpose : sets text inside cell to M for Mine if there is a mine in the cell
	* Inputs  : 2D boolean array, 2D Cell and 2D Text array
	* Outputs : 2D Cell array 
	* *****************************************************************************/
	
	private Cell [][] placeBombs(boolean [][] bombs, GameButton [][] b, Cell [][] c, Text [][] t)
	{
		//set images for bomb in here
		for(int i = 0; i < c.length; i++)
			for(int j = 0; j < c[0].length; j++)
			{
				if(bombs[i][j] == true)
				{
					c[i][j].setToken('M');
					t[i][j].setText("M");
					t[i][j].setFont(Font.font("Gill Sans", 25));
					b[i][j].setBomb(true);
					b[i][j].setTextFill(null);
				}
			}
		
		for(int i = 0; i < c.length; i++)
			for(int j = 0; j < c[0].length; j++)
			{
				if(!b[i][j].isBomb())
				{
					t[i][j].setText(findNeighbors(c, i, j));
					t[i][j].setFont(Font.font("Gill Sans", 25));
					c[i][j].setToken(t[i][j].getText().charAt(0));
					b[i][j].setText(t[i][j].getText());
					b[i][j].setTextFill(null);
					//System.out.print(c[i][j].getToken());
				}
				else
				{
					b[i][j].setText(" ");
				}
				//	System.out.print(c[i][j].getToken());
				//	continue;
				

			}
		
		return c;
	}
	
	/* *****************************************************************************
	* Name    : findNeighbors 
	* Purpose : sets all buttons visibility to false
	* Inputs  : 2D button array 
	* Outputs : 2D button array 
	* *****************************************************************************/
	
	private String findNeighbors(Cell [][] c, int x, int y)
	{
		int sum = 0;
		
		for(int i = -1; i <= 1; i++)
			for(int j = -1; j <= 1; j++)
				try
				{
					if(c[i + x][j + y].getToken() == 'M')
					{
						sum++;
					}	
				}
				catch(ArrayIndexOutOfBoundsException ex)
				{
					continue;
				}
		
		if(sum == 0)
		{
			return " ";
		}
		else
		{
			return Integer.toString(sum);
		}
		

	}
	
	/* *****************************************************************************
	* Name    : expand 
	* Purpose : sets all GameButtons visibility to false
	* Inputs  : 2D button array , int x, int y
	* Outputs : 2D button array 
	* *****************************************************************************/
	
	private void expand(GameButton [][] b, GameButton gameButton)
	{
		gameButton.setVisible(false);
		
		int xPos = gameButton.getXPos(), yPos = gameButton.getYPos();
		
		if(gameButton.getText().equals(" "))
		{
			for(int i = -1; i < 2; i++)
				for(int j = -1; j < 2; j++)
				{
					try
					{
						if(b[xPos + i][yPos + j].isVisible())
						{
							expand(b, b[xPos + i][yPos + j]);
						}
					}
					catch(ArrayIndexOutOfBoundsException ex)
					{
						continue;
					}
			}			
		}
	}
	

	/* *****************************************************************************
	* Name    : handleShowClick 
	* Purpose : listens for click to ShowBombs
	* Inputs  : 2D button array and 2D cell array
	* Outputs : none
	* *****************************************************************************/
	
	private void handleShowClick(GameButton [][] btGame, Cell [][] c)
	{
		showBombs(btGame, c);
	}
	

	
	/* *****************************************************************************
	* Name    : handleStartClick 
	* Purpose : listens for click on a bomb and showAll is called
	* Inputs  : 2D button array
	* Outputs : none
	* *****************************************************************************/
	private void handleStartClick(boolean [][] bombs, GameButton [][] bt, Cell [][] c, Text [][] t)
	{
		for(int i = 0; i < t.length; i++)
			for(int j = 0; j < t[0].length; j++)
			{
				t[i][j].setText("");
				c[i][j].setToken(' ');
				bt[i][j].setText("Test");
				bt[i][j].setTextFill(Color.BLACK);
				bt[i][j].setBomb(false);
			}
				
			
		bt = showNone(bt);
		bombs = setBombs(bombs);
		c = placeBombs(bombs, bt, c, t);		
	}
	
}
