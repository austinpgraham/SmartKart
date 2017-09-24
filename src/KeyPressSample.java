import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class KeyPressSample 
{
	public static void main(String[] args) throws AWTException
	{
		Robot r = new Robot();
		int randomNum = 0;
		
		for(int i=0; i<20; i++)
		{
			randomNum = ThreadLocalRandom.current().nextInt(1, 4);
			System.out.println(randomNum);
			if(randomNum == 1)
			{
				r.keyPress(KeyEvent.VK_SHIFT);
				r.keyPress(KeyEvent.VK_LEFT);
				r.delay(1000);
				r.keyRelease(KeyEvent.VK_SHIFT);
				r.keyRelease(KeyEvent.VK_LEFT);
				
			}
			else if(randomNum == 2)
			{
				r.keyPress(KeyEvent.VK_SHIFT);
				r.keyPress(KeyEvent.VK_RIGHT);
				r.delay(1000);
				r.keyRelease(KeyEvent.VK_SHIFT);
				r.keyRelease(KeyEvent.VK_RIGHT);
			}
			else
			{
				r.keyPress(KeyEvent.VK_SHIFT);
				r.delay(1000);
				r.keyRelease(KeyEvent.VK_SHIFT);
			}
		}
	}
}
