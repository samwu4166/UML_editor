import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class MyPanel extends JPanel{
	private ArrayList<Shape> shapeArrayList;
	private ArrayList<Line> lineArrayList;
	private int click_x;
	private int click_y;
	private Boolean isSelectItem;
	int CircleRadius;
	int RectWidth;
	int RectHeight;
	public MyPanel(GUI myGUI) {
		CircleRadius = 40;
		RectWidth = 100;
		RectHeight = 25;
		// TODO Auto-generated constructor stub
		this.setShapeArrayList(new ArrayList<Shape>());
		this.setLineArrayList(new ArrayList<Line>());
		this.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.setBounds(187, 41, 753, 484);
		this.addMouseMotionListener(new MouseAdapter() {
			@Override
		    // from MouseWheelListener
		    public void mouseWheelMoved(MouseWheelEvent e) {
		        //System.out.println("mouseWheelMoved");
		    }
			@Override
		    public void mouseDragged(MouseEvent e) {
		        int mode = myGUI.getMode();
		        if (mode==0) {
		        	//System.out.println("mouseDragged");
		        	if(getIsSelectItem()) {
		        		//System.out.println("DraggedItem");
		        		for(int i=0;i<getShapeArrayList().size();i++) {
							if(((BasicObject) getShapeArrayList().get(i)).isSelected()) {
	
								int click_x = getClick_x();
								int click_y = getClick_y();
								int moved_x = e.getX();
								int moved_y = e.getY();
		
								((BasicObject) getShapeArrayList().get(i)).pointMoved(moved_x-click_x,moved_y-click_y);
							}
						}
		        		setClick_x(e.getX());
			        	setClick_y(e.getY());
		        	}else {
		        		//System.out.println("SelectItem");
		        	}
		        }
		        repaint();
		    }
			@Override
		    public void mouseMoved(MouseEvent e) {
				repaint();
		        //System.out.println("mouseMoved");
		    }
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				//System.out.println("mouseRelease");
				int mode = myGUI.getMode();
				checkPoint startPoint = new checkPoint();
				checkPoint endPoint = new checkPoint();
				for(int i=0;i<getShapeArrayList().size();i++) {
					if(((BasicObject) getShapeArrayList().get(i)).contain(getClick_x(),getClick_y())) {
						startPoint = ((BasicObject) getShapeArrayList().get(i)).getNearestCheckPoint(getClick_x(),getClick_y());
						System.out.println(startPoint);
					}
				}
				for(int i=0;i<getShapeArrayList().size();i++) {
					if(((BasicObject) getShapeArrayList().get(i)).contain(e.getX(),e.getY())) {
						endPoint = ((BasicObject) getShapeArrayList().get(i)).getNearestCheckPoint(e.getX(),e.getY());
						System.out.println(endPoint);
					}
				}
				switch(mode) {
					case 0:
						if(!getIsSelectItem()) {
							
							int leftX = e.getX() > getClick_x() ? getClick_x():e.getX();
							int rightX = e.getX() > getClick_x() ? e.getX() : getClick_x();
							int upY = e.getY() > getClick_y() ? getClick_y() : e.getY();
							int downY = e.getY() > getClick_y() ? e.getY() : getClick_y();

							for(int i=0;i<getShapeArrayList().size();i++) {
								if(((BasicObject) getShapeArrayList().get(i)).isin(leftX,upY,rightX,downY)) {
									((BasicObject) getShapeArrayList().get(i)).setSelected(true);
									setIsSelectItem(true);
								}else {
									((BasicObject) getShapeArrayList().get(i)).setSelected(false);
								}
							}
						}
						break;
					case 1:
						if(startPoint.getBelongTo() != endPoint.getBelongTo() && startPoint.getBelongTo()!= -1 && endPoint.getBelongTo()!= -1) {
							getLineArrayList().add(new Association_Line(startPoint, endPoint, myGUI.getDotSize()));
						}
						break;
					case 2:
						if(startPoint.getBelongTo() != endPoint.getBelongTo() && startPoint.getBelongTo()!= -1 && endPoint.getBelongTo()!= -1) {
							getLineArrayList().add(new Generalization_Line(startPoint, endPoint, myGUI.getDotSize()));
						}
						break;
					case 3:
						if(startPoint.getBelongTo() != endPoint.getBelongTo() && startPoint.getBelongTo()!= -1 && endPoint.getBelongTo()!= -1) {
							getLineArrayList().add(new Composition_Line(startPoint, endPoint, myGUI.getDotSize()));
						}
						break;
					default:
						break;
				}
			}
			@Override
			public void mouseClicked(MouseEvent e) {
		        //System.out.println("mouseClicked");
		    }
			@Override
		    public void mousePressed(MouseEvent e) {
		        //System.out.println("mousePressed");
		        diselectShapeList();
		        repaint();
		        setClick_x(e.getX());
				setClick_y(e.getY());
		        int mode = myGUI.getMode();
		        if(mode < 4) {
		        	checkSelectedItem(e);
		        }
				switch(mode){
					case 0:
						break;
					case 1:
					case 2:
					case 3:
						break;
					case 4:
						System.out.println(getShapeArrayList().size()+1);
						getShapeArrayList().add(new ClassObject("default", e.getX(), e.getY(), getShapeArrayList().size()+1, RectWidth, RectHeight, myGUI.getDotSize()));
						break;
					case 5:
						System.out.println(getShapeArrayList().size()+1);
						getShapeArrayList().add(new UseCaseObject("default", e.getX(), e.getY(), getShapeArrayList().size()+1, CircleRadius, myGUI.getDotSize())); // radius = 20
						break;
					default :
						break;
				}
				repaint();
		    }
			@Override
		    public void mouseEntered(MouseEvent e) {
		        //System.out.println("mouseEntered");
		    }
			@Override
		    public void mouseExited(MouseEvent e) {
		        //System.out.println("mouseExited");
		    }
		});
		
	}
	public void checkSelectedItem(MouseEvent e) {
		setIsSelectItem(false);
		// set selected item by order
		int max_depth = -999;
		int max_index = -1;
		for(int i=0;i<getShapeArrayList().size();i++) {
			if(((BasicObject) getShapeArrayList().get(i)).contain(e.getX(),e.getY())) {
				if(getShapeArrayList().get(i).getDepth() > max_depth) {
					max_depth = getShapeArrayList().get(i).getDepth();
					max_index = i;
				}
				setIsSelectItem(true);
			}else {
				((BasicObject) getShapeArrayList().get(i)).setSelected(false);
			}
		}
		if(max_index != -1) {
			System.out.println("grep depth : "+max_index);
			diselectShapeList();
			((BasicObject) getShapeArrayList().get(max_index)).setSelected(true);
		}
	}
	public void diselectShapeList() {
		for(int i=0;i<getShapeArrayList().size();i++) {((BasicObject) getShapeArrayList().get(i)).setSelected(false);}
	}
	public void paintComponent(Graphics g) {
        super.paintComponent(g);       
        
        for(int i=0;i<this.getShapeArrayList().size();i++) {
        	//System.out.println(((BasicObject) this.getShapeArrayList().get(i)).isSelected());
        	this.getShapeArrayList().get(i).draw(g);
        }
        for(int i=0;i<this.getLineArrayList().size();i++) {
        	this.getLineArrayList().get(i).draw(g);
        }
    }
	public ArrayList<Shape> getShapeArrayList() {
		return shapeArrayList;
	}
	public void setShapeArrayList(ArrayList<Shape> shapeArrayList) {
		this.shapeArrayList = (ArrayList<Shape>) shapeArrayList;
	}
	
	public int getClick_x() {
		return click_x;
	}
	public void setClick_x(int click_x) {
		this.click_x = click_x;
	}
	public int getClick_y() {
		return click_y;
	}
	public void setClick_y(int click_y) {
		this.click_y = click_y;
	}
	public Boolean getIsSelectItem() {
		return isSelectItem;
	}
	public void setIsSelectItem(Boolean isSelectItem) {
		this.isSelectItem = isSelectItem;
	}
	public ArrayList<Line> getLineArrayList() {
		return lineArrayList;
	}
	public void setLineArrayList(ArrayList<Line> lineArrayList) {
		this.lineArrayList = lineArrayList;
	}  

}