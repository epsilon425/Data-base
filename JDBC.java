import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import java.text.DecimalFormat;
public class JDBC  
{
   public static JFrame mainFrame;

   public static JLabel DBName = new JLabel("Database: ");
   public static JTextField Db = new JTextField();
   public static JButton Select = new JButton("Select");
   public static JButton Execute = new JButton("Execute");


   public static JLabel ColumnLabel = new JLabel("Column: ");
   public static JTextField Column = new JTextField();

   public static JTextField MaxText = new JTextField();
   public static JButton Max = new JButton("Maximum");
   public static JTextField MinText = new JTextField();
   public static JButton Min = new JButton("Minimum");
   public static JTextField AvgText = new JTextField();
   public static JButton Avg = new JButton("Average");
   public static JTextField MedianText = new JTextField();
   public static JButton Median = new JButton("Median");
   public static ResultSet rset;
   public static JTextArea Input = new JTextArea();
   public static JTextArea Output = new JTextArea();
   public static Connection conn = null;
   public static Statement stmt = null;
   public static int rowCount=0;
   public static void main( String[] args )
   {  
      Font ss_font = new Font("SansSarif",Font.BOLD,16) ;
      Font ms_font = new Font("Monospaced",Font.BOLD,16) ;

      JPanel P1 = new JPanel();   // Top panel
      JPanel P2 = new JPanel();

      P1.setLayout( new BorderLayout() );
      P2.setLayout( new BorderLayout() );

      /* =============================================
         Make top panel
         ============================================= */
      JScrollPane d1 = new JScrollPane(Input, 
                                       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS ,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      Input.setFont( ss_font );

      JPanel s1 = new JPanel(); // Side panel
      s1.setLayout( new GridLayout( 8,1 ) );
      s1.add( DBName );
      s1.add( Db );
      Db.setFont( ss_font );
      s1.add( Select );
      s1.add( Execute );
      Execute.setPreferredSize(new Dimension(140, 30)) ;

      P1.add(d1, "Center");
      P1.add(s1, "East");

      /* =============================================
         Make bottom panel
         ============================================= */
      JScrollPane d2 = new JScrollPane(Output, 
                                       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS ,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

      Output.setFont( ms_font );
      Output.setEditable(false);

      JPanel s3 = new JPanel(); // Put ColumnLabel and Column on 1 row
      s3.add(ColumnLabel);
      s3.add(Column);
      Column.setFont( ss_font );

      Column.setPreferredSize(new Dimension(40, 30)) ;

      JPanel s2 = new JPanel(); // Side panel
      s2.setLayout( new GridLayout( 10,1 ) );
      s2.add( s3 );

      MaxText.setPreferredSize(new Dimension(140, 30)) ;
      s2.add( MaxText );
      MaxText.setFont( ss_font );
      MaxText.setEditable(false);
      MaxText.setVisible(true);
      s2.add( Max );

      s2.add( MinText );
      MinText.setFont( ss_font );
      MinText.setEditable(false);
      MinText.setVisible(true);
      s2.add( Min );

      s2.add( AvgText );
      AvgText.setFont( ss_font );
      AvgText.setEditable(false);
      AvgText.setVisible(true);
      s2.add( Avg );

      s2.add( MedianText );
      MedianText.setFont( ss_font );
      MedianText.setEditable(false);
      MedianText.setVisible(true);
      s2.add( Median );


      P2.add(d2, "Center");
      P2.add(s2, "East");

      mainFrame = new JFrame("Default GUI for CS377 JDBC project");
      mainFrame.getContentPane().setLayout( new GridLayout(2,1) );
      mainFrame.getContentPane().add( P1 );
      mainFrame.getContentPane().add( P2 );
      mainFrame.setSize(900, 700);

      mainFrame.setVisible(true);
      Select.addActionListener(new dbGetter());
      Execute.addActionListener(new queryGetter());     	  
      Max.addActionListener(new Maxim()); 	
      Min.addActionListener(new Minim()); 
      Avg.addActionListener(new Average());
      Median.addActionListener(new Median());
    }
public static class dbGetter implements ActionListener 
{
	public void actionPerformed(ActionEvent event)
	{
	String dbName = Db.getText();
        String url = "jdbc:mysql://holland.mathcs.emory.edu:3306/";
        String userName = "cs377";
        String password = "abc123";
	Output.setText("");
	try
	{
		Class.forName("com.mysql.jdbc.Driver");
	}
	catch (Exception e)
	{
		System.out.println("Failed to load JDBC driver.");
		return;	
	}


	try
	{
		conn = DriverManager.getConnection(url+dbName,userName,password);
		stmt = conn.createStatement();
	}
	
	catch (Exception e)
	{
		Output.append("Can't connect to the db or it doesn't exist "+dbName);
		return;
	}
	if(dbName.equals(""))Output.append("Please select the correct DB");
		else
		Output.append("selected "+dbName);
        } 
	
		
}

public static class queryGetter implements ActionListener
{
	public void actionPerformed(ActionEvent event)
	{	
	rowCount=0;
        int[] length = new int[100];      // Max 100 fields....
	String query;
	query = Input.getText();
	Output.setText("");
	while(true)
	{
		try
		{
			rset = stmt.executeQuery(query);
			ResultSetMetaData meta = rset.getMetaData();
			int NCols = meta.getColumnCount();
			for ( int i=1; i<= NCols; i++)
			{
				String name;
				name = meta.getColumnLabel(i);	
				int namelength = name.length();
				if(namelength>6)
				name = name.substring(0,6);
				Output.append(name);
				
				length[i] = Math.max( 6,
					    meta.getColumnDisplaySize(i));			
				
			
			
			for(int j= name.length(); j<length[i];j++)
				Output.append(" ");
			}
			Output.append("\n");
			
			for(int i=1;i<=NCols;i++)
				{
				for(int j=0;j<length[i]-1;j++)
					Output.append("*");
				Output.append(" ");
				}
			Output.append("\n");
			while(rset.next())
			{
				for(int i=1;i<=NCols;i++)
				{
				
					String nextItem;
					nextItem = rset.getString(i);
					if (!(nextItem==null))
						{
							if(meta.getColumnType(i)==Types.DECIMAL || meta.getColumnType(i) ==Types.INTEGER)
							{
								int numlength = nextItem.length();
								if (numlength>=10) numlength=10;
								nextItem = nextItem.substring(0,numlength);
								
								numlength = nextItem.length();	
								for(int j=0;j<length[i]-numlength;j++)
								Output.append(" ");
											
								
								Output.append(nextItem);
							}
								else
								{

									Output.append(nextItem);
									for(int j=nextItem.length();j<length[i];j++)
									Output.append(" ");

								}						
						}
						else
							{
								nextItem="NULL";
								if(meta.getColumnType(i)==Types.DECIMAL || meta.getColumnType(i) ==Types.INTEGER)
								{
									int numlength = nextItem.length();	
									for(int j=0;j<length[i]-numlength;j++)
										Output.append(" ");	
								
									Output.append(nextItem);
								
								}

									else
									{

										Output.append(nextItem);
										for(int j=nextItem.length();j<length[i];j++)
											Output.append(" ");

									}	
								
								
							}
							

				}
				rowCount++;		
				Output.append("\n");

			}
			
			//rset.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	return;
	}
	}

}



//Column
//Max

public static class Maxim implements ActionListener
{
	public void actionPerformed(ActionEvent event)
	{	ResultSetMetaData secondMeta;
		int columnNum=Integer.parseInt(Column.getText());
		int type;
		String[] wordsArr=new String[rowCount+1];
		int count=0;
		double max=0;
		String maxS="default";
		try
		{
			rset.beforeFirst();
			secondMeta = rset.getMetaData();
			if(!(0<columnNum&&columnNum<=secondMeta.getColumnCount()))
				{
					return;
				}
						
		        type = secondMeta.getColumnType(columnNum);	
			if(type==1)
			{
				rset.next();
				while(rset.next())
				{

					String test = rset.getString(columnNum);
					if(test!=null)
					{					
						maxS=rset.getString(columnNum);
						break;
					}			
				
				}
				rset.beforeFirst();
				while(rset.next())
				{	
					String test = rset.getString(columnNum);
					if(test==null)continue;
					if(maxS.compareTo(rset.getString(columnNum))<0)
					 	maxS=rset.getString(columnNum);
				}
			}
			else if(type==3)
			{
				while(rset.next())
				{	if(rset.getString(columnNum)==null)continue;
					if(rset.getDouble(columnNum)>max) max = rset.getDouble(columnNum);	   
				}
			}
			else if(type==4)
			{
				while(rset.next())
				{	if(rset.getString(columnNum)==null)continue; 
					if(rset.getDouble(columnNum)>max) max = rset.getDouble(columnNum); 
				}
			}

			if(!(type==1))
			MaxText.setText(Double.toString(max));
				else MaxText.setText(maxS);
		}
		
		
		catch(Exception e)
		{
		  System.out.println(e.getMessage());
		}
	}
}


public static class Minim implements ActionListener
{
	public void actionPerformed(ActionEvent event)
	{	ResultSetMetaData secondMeta;
		int columnNum=Integer.parseInt(Column.getText());
		int type;
		String[] wordsArr=new String[rowCount+1];
		int count=0;
		double mins=1000000000;
		String minS="default";
		try
		{
			rset.beforeFirst();
			secondMeta = rset.getMetaData();
		        type = secondMeta.getColumnType(columnNum);	
			if(type==1)
			{	
				while(rset.next())
				{
					String test = rset.getString(columnNum);
					if(test!=null) 
					{				
						minS=rset.getString(columnNum);
						break;
					}
				}
				rset.beforeFirst();
				while(rset.next())
				{
					String test = rset.getString(columnNum);
					if(test==null)continue;
					if(minS.compareTo(rset.getString(columnNum))>0)
						minS=rset.getString(columnNum);		
				}
			}
			else if(type==3)
			{
				
				while(rset.next())
				{
					if(rset.getString(columnNum)==null)continue;
					if(rset.getDouble(columnNum)<mins) mins = rset.getDouble(columnNum);   
				}
			}
			else if(type==4)
			{
				
				while(rset.next())
				{
					if(rset.getString(columnNum)==null)continue; 
					if(rset.getDouble(columnNum)<mins) mins = rset.getDouble(columnNum); 
				}
			}
			if(!(type==1))
			MinText.setText(Double.toString(mins));
				else MinText.setText(minS);
		}
		
		
		catch(Exception e)
		{
		  System.out.println(e.getMessage());
		}
	}

}


public static class Average implements ActionListener
{
	public void actionPerformed(ActionEvent event)
	{	
		ResultSetMetaData secondMeta;
		int columnNum=Integer.parseInt(Column.getText());
		int type;
		double average=0;

		try
		{
			rset.beforeFirst();
			secondMeta = rset.getMetaData();
			if(!(0<columnNum&&columnNum<=secondMeta.getColumnCount()))
			{

				return;

			}
			type = secondMeta.getColumnType(columnNum);
			if(type==3||type==4)
			{	while(rset.next())
				{
					average = average+rset.getDouble(columnNum);
				}
			}
				else
				{
					AvgText.setText("Illegal");
					return;	
				}
			DecimalFormat df = new DecimalFormat("0.00");
			average = average/(rowCount+1);
			
			String averageS = Double.toString(average);	
			AvgText.setText(averageS);
		}

		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

	}

}


public static class Median implements ActionListener
{
	public void actionPerformed(ActionEvent event)
	{

	ResultSetMetaData secondMeta;
	int columnNum = Integer.parseInt(Column.getText());
	int type;
	int count=0;
	String nullReplace="default";
	ArrayList<String> words = new ArrayList();
		try
		{
			rset.beforeFirst();
			secondMeta = rset.getMetaData();

			if(!(0<columnNum&columnNum<=secondMeta.getColumnCount()))
			{
				return;
			}
		
			type = secondMeta.getColumnType(columnNum);
				
			if(type==1)
			{

				while(rset.next())
				{
					String test = rset.getString(columnNum);
					if(test==null)
					{
						continue;
					}
					else
					{
						words.add(test);
						count++;
					}
				}
				String[] wordsar = words.toArray(new String[0]);
				Arrays.sort(wordsar);
				for(String s:wordsar) System.out.println(s);
				System.out.println("Count: "+count);
				if((count%2)==0)
				MedianText.setText(wordsar[(count/2)+1]);
					else MedianText.setText(wordsar[count/2]);
			}

			if(type==3||type==4)
			{
				while(rset.next())
				{
					String test = rset.getString(columnNum);
					if(test==null)
					{
						continue;
					}
					else
					{
						words.add(test);
						count++;
					}
				
				}
				String[] wordsar = words.toArray(new String[0]);
				Arrays.sort(wordsar);
				for(String s:wordsar) System.out.println(s);
				System.out.println("Count: "+count);
				System.out.println((count/2)+1);
				if((count%2)==0)
				MedianText.setText(wordsar[(count/2)+1]);
					else MedianText.setText(wordsar[(count/2)]);
					

			}

			
		}
		catch(Exception e)
		{

			System.out.println(e.getMessage());		

		}		

	}

}




}
