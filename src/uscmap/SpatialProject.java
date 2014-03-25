package uscmap;

import java.awt.Color;
import java.awt.Graphics;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import oracle.sql.STRUCT;
import oracle.spatial.geometry.*;
import dbc.DBConnection;

public class SpatialProject extends JFrame implements MouseMotionListener, MouseListener 
{
	private final int xOffset = 8;
    private final int yOffset = 31;
    private boolean boolPointClicked = false;
    private boolean boolStudentClicked = false;
    private boolean boolNoMoreDrawing = false;
    private boolean boolEmergencyClicked = false;
    private int intNumQueries;
    private java.util.List polygonForRange = new ArrayList();
    private java.util.List studentsForEmergency = new ArrayList();
    private java.util.List emergencyAS = new ArrayList();
    private java.util.List buildingPoint = new ArrayList();
    private java.util.List studentPoint = new ArrayList();
    private java.util.List asPoint = new ArrayList();
    private final int pointQueryRadius = 50;
    private String strASValue = "";
    
    public SpatialProject()
    {
    	initComponents();
        lblSelected.setText("");
        lblSelectedValue.setText("");
        lblSelected.setVisible(false);
        lblSelectedValue.setVisible(false);
        lblHiddenAnValue.setVisible(false);
        lblHiddenAnValue.setText("");
        intNumQueries = 0;
        //polygonForRange = new int[30];
        
        addMouseMotionListener(this);
        addMouseListener(this);
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        
         int mX = ( (int) e.getPoint().getX() - 8);
         int mY = ( (int) e.getPoint().getY() - 31);
         if( mX <=820 & mY <= 580)
         {
            lblValueCoordinates.setText(mX + "," + mY);
         }
        //repaint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }
    
    @Override
    public void mouseClicked(MouseEvent e){
        
        int mX = ( (int) e.getPoint().getX() - 8);
        int mY = ( (int) e.getPoint().getY() - 31);
        
        if(rbPoint.isSelected())
        {
            pointClicked(mX,mY);
        }
        
        if(rbStudent.isSelected())
        {
            
            studentClicked(mX,mY);
        }
        
        if(rbEmergency.isSelected())
        {
            emergencyClicked(mX,mY);
        }
        
        if(rbRange.isSelected())
        {
            if( mX <=820 & mY <= 580)
            {
                if(e.getModifiers() == MouseEvent.BUTTON3_MASK & polygonForRange.size() <= 4)
                {
                    JOptionPane.showMessageDialog(this, "Select atleast 3 points");
                }
                else
                {
                    rangeClicked(mX,mY,e);
                }
            }
        }
        
    }
    
    private void emergencyClicked(int mX, int mY)
    {
        if(!boolEmergencyClicked)
        {
            boolEmergencyClicked = true;
            lblSelected.setText("Selected Point");
                       
                        
            if( mX <=820 & mY <= 580)
            {
                lblSelectedValue.setText(mX + "," + mY);
            }
            
            String query = "select * from ansys a " + 
                            "where SDO_NN(a.circles, " +
                            "SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(" + lblSelectedValue.getText().trim() +
                            ", NULL),NULL,NULL) , 'sdo_num_res=1') = 'TRUE'";
            
            drawAnnouncementSystems(query, Color.yellow);
            surroundingStudentQuery();
            
        }
        else
        {
            clearEverything();
        }
    }
    
    private void rangeClicked(int mX, int mY,MouseEvent e)
    {
        if(boolNoMoreDrawing)
        {
             JOptionPane.showMessageDialog(this, "No more drawing");
        }
        else
        {
                       
            Graphics g = this.getGraphics();
            g.setColor(Color.red);
            
            
            
            if(e.getModifiers() == MouseEvent.BUTTON3_MASK)
            {
              
                int intPts = polygonForRange.size();
                int x1 = (int)polygonForRange.get(0);
                int y1 = (int)polygonForRange.get(1);
                int x2 =  (int)polygonForRange.get(intPts-2);
                int y2 = (int)polygonForRange.get(intPts-1);
                g.drawLine(x2,y2,x1,y1);
                boolNoMoreDrawing = true;
                
                polygonForRange.add(polygonForRange.get(0));
                polygonForRange.add(polygonForRange.get(1));
                
                
            }
            else
            {
                polygonForRange.add(mX+xOffset);
                polygonForRange.add(mY+yOffset);
                
                int intPts = polygonForRange.size();
                
                if(intPts > 2)
                {
                    int x1 = (int)polygonForRange.get(intPts-4);
                    int y1 = (int)polygonForRange.get(intPts-3);
                    int x2 =  (int)polygonForRange.get(intPts-2);
                    int y2 = (int)polygonForRange.get(intPts-1);
                    g.drawLine(x1,y1,x2,y2);
                }
            }
            
            
        }
        
    }
    
    private void studentClicked(int mX,int mY)
    {
        if(!boolStudentClicked)
        {
            boolStudentClicked = true;
            lblSelected.setText("Selected Point");
                       
                        
            if( mX <=820 & mY <= 580)
            {
                lblSelectedValue.setText(mX + "," + mY);
            }
            
            String query = "select * from ansys a " +
                            "where SDO_NN(a.circles, " +
                            "SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(" + lblSelectedValue.getText().trim() +
                            ", NULL),NULL,NULL) , 'sdo_num_res=1') = 'TRUE'";
            drawAnnouncementSystems(query, Color.yellow);
            
            
        }
        else
        {
            clearEverything();
        }
    }
    
    private void pointClicked(int mX,int mY)
    {
        if(!boolPointClicked)
        {
            Graphics g = this.getGraphics();
            boolPointClicked = true;
            lblSelected.setText("Selected Point");
            //lblSelectedValue.setVisible(true);
            
                        
            if( mX <=820 & mY <= 580)
            {
                lblSelectedValue.setText(mX + "," + mY);
            }
            
            g.setColor(Color.red);
            g.fillRect(mX-2 + xOffset, mY-2+yOffset, 5, 5);
            g.drawOval(mX-pointQueryRadius + xOffset, mY - pointQueryRadius + yOffset, 2*pointQueryRadius, 2*pointQueryRadius);
        }
        else
        {
            clearEverything();
        }
        
    }
    
    private void clearEverything()
    {
            boolPointClicked = false;
            boolStudentClicked = false;
            boolNoMoreDrawing = false;
            boolEmergencyClicked = false;
            lblSelected.setText("");
            lblSelectedValue.setText("");
            lblHiddenAnValue.setText("");
            strASValue = "";
            polygonForRange.clear();
            studentsForEmergency.clear();
            emergencyAS.clear();
            studentPoint.clear();
            asPoint.clear();
            buildingPoint.clear();
            this.repaint();
    }
    
    @Override
    public void mouseExited(MouseEvent e){
        
    }
  
    
                             
    private void initComponents() {

        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        cbAnnounce = new javax.swing.JCheckBox();
        cbBuilding = new javax.swing.JCheckBox();
        cbStudents = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        rbWhole = new javax.swing.JRadioButton();
        rbPoint = new javax.swing.JRadioButton();
        rbRange = new javax.swing.JRadioButton();
        rbStudent = new javax.swing.JRadioButton();
        rbEmergency = new javax.swing.JRadioButton();
        btnQuery = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        lblCoordinates = new javax.swing.JLabel();
        lblValueCoordinates = new javax.swing.JLabel();
        lblSelected = new javax.swing.JLabel();
        lblSelectedValue = new javax.swing.JLabel();
        lblHiddenAnValue = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAQueries = new javax.swing.JTextArea();

        buttonGroup2.add(rbWhole);
        buttonGroup2.add(rbRange);
        buttonGroup2.add(rbPoint);
        buttonGroup2.add(rbStudent);
        buttonGroup2.add(rbEmergency);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Amandeep Singh - USC Map");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Active Features"));
        jPanel1.setName("Active Features"); // NOI18N

        cbAnnounce.setText("Announcement System");
        cbAnnounce.setToolTipText("");

        cbBuilding.setText("Building");

        cbStudents.setText("Students");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbAnnounce)
                    .addComponent(cbBuilding)
                    .addComponent(cbStudents)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbAnnounce)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbBuilding)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbStudents)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Queries"));

        rbWhole.setSelected(true);
        rbWhole.setText("Whole Region");
        rbWhole.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbWholeItemStateChanged(evt);
            }
        });

        rbPoint.setText("Point Query");
        rbPoint.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbPointItemStateChanged(evt);
            }
        });

        rbRange.setText("Range Query");
        rbRange.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbRangeItemStateChanged(evt);
            }
        });

        rbStudent.setText("Surrounding Student");
        rbStudent.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbStudentItemStateChanged(evt);
            }
        });

        rbEmergency.setText("Emergency Query");
        rbEmergency.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbEmergencyItemStateChanged(evt);
            }
        });

        btnQuery.setText("Query");
        btnQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQueryActionPerformed(evt);
            }
        });

        btnClear.setText("Clear Map");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbWhole)
                            .addComponent(rbPoint)
                            .addComponent(rbRange)
                            .addComponent(rbStudent)
                            .addComponent(rbEmergency)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(btnQuery))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(btnClear)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbWhole)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbPoint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbRange)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbStudent)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbEmergency)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnQuery)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(btnClear))
        );

        lblCoordinates.setText("Coordinates");

        lblSelected.setText("Selected Point");

        lblHiddenAnValue.setText("jLabel1");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(lblSelected)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblSelectedValue))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(lblCoordinates)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblValueCoordinates))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblHiddenAnValue)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHiddenAnValue)
                .addGap(26, 26, 26)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCoordinates)
                    .addComponent(lblValueCoordinates))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSelected)
                    .addComponent(lblSelectedValue))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hw2/map.jpg"))); // NOI18N

        txtAQueries.setEditable(false);
        txtAQueries.setColumns(20);
        txtAQueries.setLineWrap(true);
        txtAQueries.setRows(5);
        txtAQueries.setAutoscrolls(false);
        txtAQueries.setMaximumSize(new java.awt.Dimension(104, 22));
        jScrollPane1.setViewportView(txtAQueries);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 840, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>                        

    private void btnQueryActionPerformed(ActionEvent evt) {                                         
        
        if(rbWhole.isSelected())
        {
            if(!cbAnnounce.isSelected() & !cbStudents.isSelected() & !cbBuilding.isSelected())
            {
                JOptionPane.showMessageDialog(this, "Please select atleast one Active Feature");
            }
            else
            {
                wholeRegionQuery();
            }
        }
        
        if(rbPoint.isSelected())
        {
            if(!cbAnnounce.isSelected() & !cbStudents.isSelected() & !cbBuilding.isSelected())
            {
                JOptionPane.showMessageDialog(this, "Please select atleast one Active Feature");
            }
            else
            {
                pointQuery();
            }
        }
        
        if(rbStudent.isSelected())
        {
            surroundingStudentQuery();
        }
        if(rbRange.isSelected())
        {
            if(!cbAnnounce.isSelected() & !cbStudents.isSelected() & !cbBuilding.isSelected())
            {
                JOptionPane.showMessageDialog(this, "Please select atleast one Active Feature");
            }
            else
            {
                rangeQuery();
            }
        }
        if(rbEmergency.isSelected())
        {
            emergencyQuery();
        }
    }                                        

    private void emergencyQuery()
    {
        try
        {
        if(studentsForEmergency.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "No Students currently in vicinity");
        }
        else
        {
            DBConnection dbConn = new DBConnection();
            
            Graphics gr = this.getGraphics();
            
                        
            for(int i=0 ; i < studentsForEmergency.size() - 1; i = i+2 )
            {
                String query = "select a.asid,a.circles,a.radius, sdo_nn_distance (1) distance " +
                                "from ansys a " +
                                "where SDO_NN(a.circles,SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(" +
                                studentsForEmergency.get(i) + "," + studentsForEmergency.get(i+1) +
                                ", NULL),NULL,NULL) , 'sdo_num_res=2',1) = 'TRUE' " +
                                "order by distance desc";
                
                ResultSet rs = dbConn.getSpatialData(query);
                
                //display the query in he text area
                intNumQueries = intNumQueries + 1;
                txtAQueries.append("\n" +" Query:" + intNumQueries + " - " + query);
                
                rs.next(); //only need the first record, as it is the second farthest from the given point
                
                if(rs.getString("asid").equals(strASValue.trim()))
                {
                    rs.next(); //in some cases he distance is coming as zero,
                    
                }
                
                String strAsid= rs.getString("asid").trim();
                int intRadius = rs.getInt("radius");
                Object st = rs.getObject("circles");
                STRUCT strct = (STRUCT) st;
                JGeometry geoData = JGeometry.load(strct);
                double[] points = geoData.getOrdinatesArray();
                
                if(emergencyAS.contains(strAsid))
                {
                    gr.setColor((Color) emergencyAS.get(emergencyAS.indexOf(strAsid) + 1));
                }
                else
                {
                    Random rand = new Random();
                    float r = rand.nextFloat();
                    float g = rand.nextFloat();
                    float b = rand.nextFloat();
                    
                    Color colorRandom = new Color(r,g,b).brighter();
                    emergencyAS.add(strAsid);
                    emergencyAS.add(colorRandom);
                    
                    gr.setColor(colorRandom); //draw students
                    
                }
                
                    //draw students
                    gr.fillRect((int)studentsForEmergency.get(i) + xOffset-5, (int)studentsForEmergency.get(i+1)+yOffset-5,10,10);
                    //draw annoucement systems
                    gr.fillRect((int)points[0] + xOffset-7, (int)points[1]+yOffset-intRadius-7,15,15);
                    gr.drawOval((int)points[0] - intRadius + xOffset, (int)points[1] + yOffset- (2*intRadius), 2*intRadius, 2*intRadius);
                
                    
                
            }
        }
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(this, "SQL Exception in EmergencyQuery :" + e.toString());
        }
    }
    
    private void rangeQuery()
    {
        if(polygonForRange.isEmpty() || !boolNoMoreDrawing )
        {
            JOptionPane.showMessageDialog(this, "Draw the complete polygon first");
        }
        else
        {
            String strPolygon = "";
            
            int intPts = polygonForRange.size();
            
            for (int i=0 ; i < intPts; i++)
            {
                if(i == intPts-1)
                {
                   strPolygon = strPolygon + ((int)polygonForRange.get(i)-yOffset);
                }
                else
                {
                    if(i % 2 == 0)
                    {
                        strPolygon = strPolygon + ((int)polygonForRange.get(i)- xOffset)+ ",";
                    }
                    else
                    {
                        strPolygon = strPolygon + ((int)polygonForRange.get(i)- yOffset)+ ",";
                    }
                    
                }
            }
            
            
            
            
            String strBuildingsQuery = "select * " +
                            "from buildings " +
                            "where SDO_RELATE(shape, " +
                            "SDO_GEOMETRY(2003, " +
                            "null, null, " +
                            "SDO_ELEM_INFO_ARRAY(1,1003,1)," +
                            "SDO_ORDINATE_ARRAY(" + strPolygon + ")), 'mask=anyinteract') = 'TRUE'";
            
            if(cbBuilding.isSelected())
            {
                drawBuldings(strBuildingsQuery, Color.yellow);
            }
            
            String strAnnounceQuery = "select * " +
                            "from ansys " +
                            "where SDO_RELATE(circles, " +
                            "SDO_GEOMETRY(2003, " +
                            "null, null, " +
                            "SDO_ELEM_INFO_ARRAY(1,1003,1)," +
                            "SDO_ORDINATE_ARRAY(" + strPolygon + ")), 'mask=anyinteract') = 'TRUE'";
            
            if(cbAnnounce.isSelected())
            {
                drawAnnouncementSystems(strAnnounceQuery, Color.red);
            }
            
            String strStudentsQuery = "select * " +
                            "from students " +
                            "where SDO_RELATE(points, " +
                            "SDO_GEOMETRY(2003, " +
                            "null, null, " +
                            "SDO_ELEM_INFO_ARRAY(1,1003,1)," +
                            "SDO_ORDINATE_ARRAY(" + strPolygon + ")), 'mask=inside') = 'TRUE'";
            
            if(cbStudents.isSelected())
            {
                drawStudents(strStudentsQuery, Color.green);
            }
          
        }
    }
    private void surroundingStudentQuery()
    {
        if(lblHiddenAnValue.getText().trim().equalsIgnoreCase(""))
        {
            JOptionPane.showMessageDialog(this, "Please select a point first");
        }
        else
        {
            String strStudentsQuery = "select * " +
                            "from students " +
                            "where SDO_RELATE(points, " +
                            "SDO_GEOMETRY(2003, " +
                            "null, null, " +
                            "SDO_ELEM_INFO_ARRAY(1,1003,4)," +
                            "SDO_ORDINATE_ARRAY(" + lblHiddenAnValue.getText().trim() + ")), 'mask=anyinteract') = 'TRUE'";
            
            drawStudents(strStudentsQuery, Color.yellow);
        }
    }
    
    private void pointQuery()
    {
        if(lblSelectedValue.getText().equalsIgnoreCase(""))
        {
            JOptionPane.showMessageDialog(this, "Please select a point first");
        }
        else
        {
            String strPoint = lblSelectedValue.getText().trim();
            
            java.util.List<String> strPoints = Arrays.asList(strPoint.split(","));
            
            int intX = Integer.parseInt(strPoints.get(0));
            int intY = Integer.parseInt(strPoints.get(1));
            
            String strBuildingQuery = "select * " +
                            "from buildings " +
                            "where SDO_RELATE(shape, " +
                            "SDO_GEOMETRY(2003, " +
                            "null, null, " +
                            "SDO_ELEM_INFO_ARRAY(1,1003,4)," +
                            "SDO_ORDINATE_ARRAY(" + intX + "," + (intY + pointQueryRadius) + "," +
                            intX + "," + (intY - pointQueryRadius) + "," +
                            (intX - pointQueryRadius) + "," + intY + ")), 'mask=anyinteract') = 'TRUE'";
            
            String strStudentsQuery = "select * " +
                            "from students " +
                            "where SDO_RELATE(points, " +
                            "SDO_GEOMETRY(2003, " +
                            "null, null, " +
                            "SDO_ELEM_INFO_ARRAY(1,1003,4)," +
                            "SDO_ORDINATE_ARRAY(" + intX + "," + (intY + pointQueryRadius) + "," +
                            intX + "," + (intY - pointQueryRadius) + "," +
                            (intX - pointQueryRadius) + "," + intY + ")), 'mask=anyinteract') = 'TRUE'";
            
            String strAnnounceQuery = "select * " +
                            "from ansys " +
                            "where SDO_RELATE(circles, " +
                            "SDO_GEOMETRY(2003, " +
                            "null, null, " +
                            "SDO_ELEM_INFO_ARRAY(1,1003,4)," +
                            "SDO_ORDINATE_ARRAY(" + intX + "," + (intY + pointQueryRadius) + "," +
                            intX + "," + (intY - pointQueryRadius) + "," +
                            (intX - pointQueryRadius) + "," + intY + ")), 'mask=anyinteract') = 'TRUE'";
            
            Color color = Color.green;
            
            if(cbBuilding.isSelected())
            {
                drawBuldings(strBuildingQuery,color);
            }
            if(cbAnnounce.isSelected())
            {
                drawAnnouncementSystems(strAnnounceQuery,color);
            }
            if(cbStudents.isSelected())
            {
                drawStudents(strStudentsQuery,color);
            }
            
            
            color = Color.yellow;
            
            String strNearestBuilding = "select b.bid,b.bname,b.num_vertices,b.shape, sdo_nn_distance (1) distance " + 
                                " from buildings  b " +
                                " where SDO_NN(b.shape,SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE( " +
                                lblSelectedValue.getText().trim() +
                                ", NULL),NULL,NULL) , 'sdo_num_res=1',1) = 'TRUE' "+
                                "and b.bid in(" + 
                                createCommaSeparatedString(buildingPoint) +  ")";
            
            String strNearestStudent = "select s.sid,s.points, sdo_nn_distance (1) distance " + 
                                " from students  s " +
                                " where SDO_NN(s.points,SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE( " +
                                lblSelectedValue.getText().trim() +
                                ", NULL),NULL,NULL) , 'sdo_num_res=1',1) = 'TRUE' "+
                                "and s.sid in(" + 
                                createCommaSeparatedString(studentPoint) +  ")";
            
            String strNearestAS = "select a.asid,a.circles,a.radius, sdo_nn_distance (1) distance " + 
                                " from ansys a " +
                                " where SDO_NN(a.circles,SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE( " +
                                lblSelectedValue.getText().trim() +
                                ", NULL),NULL,NULL) , 'sdo_num_res=1',1) = 'TRUE' "+
                                "and a.asid in(" + 
                                createCommaSeparatedString(asPoint) +  ")";
            
            if(cbBuilding.isSelected() & !buildingPoint.isEmpty())
            {
                drawBuldings(strNearestBuilding,color);
            }
            if(cbAnnounce.isSelected() & !asPoint.isEmpty())
            {
                drawAnnouncementSystems(strNearestAS,color);
            }
            if(cbStudents.isSelected() & !studentPoint.isEmpty())
            {
                drawStudents(strNearestStudent,color);
            }
            
            
        }
    }
    
    private String createCommaSeparatedString(java.util.List list)
    {
        String str = "";
        for(int i=0;i<=list.size()-1;i++)
        {
            if(i== list.size() - 1)
            {
                str = str +  "'" + list.get(i) + "'";
            }
            else
            {
                str = str + "'" + list.get(i) + "',";
            }
        }
        
        return str;
    }
    
    private void wholeRegionQuery(){
            if(cbBuilding.isSelected())
            {
                String query = "select * from buildings";
                drawBuldings(query,Color.yellow);
            }
            if(cbAnnounce.isSelected())
            {
                String query = "select * from ansys";
                drawAnnouncementSystems(query,Color.red);
            }
            if(cbStudents.isSelected())
            {
                String query = "select * from students";
                drawStudents(query,Color.green);
            }
    }
    
    private void rbWholeItemStateChanged(java.awt.event.ItemEvent evt) {                                         
        clearEverything();
    }                                        

    private void rbPointItemStateChanged(java.awt.event.ItemEvent evt) {                                         
       clearEverything();
    }                                        

    private void rbRangeItemStateChanged(java.awt.event.ItemEvent evt) {                                         
        clearEverything();
    }                                        

    private void rbStudentItemStateChanged(java.awt.event.ItemEvent evt) {                                           
        clearEverything();
    }                                          

    private void rbEmergencyItemStateChanged(java.awt.event.ItemEvent evt) {                                             
        clearEverything();
    }                                            

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {                                         
        clearEverything();
    }                                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                SpatialProject objHW = new SpatialProject();
                objHW.setVisible(true);
               
                
            }
        });
    }
    
    public void drawBuldings(String query,Color color){
        try{
            
            DBConnection dbConn = new DBConnection();
            
            ResultSet rs = dbConn.getSpatialData(query);
            
            //output the query
            intNumQueries = intNumQueries + 1;
            txtAQueries.append("\n" +" Query:" + intNumQueries + " - " + query);
                        
            Graphics g = this.getGraphics();
            
            while(rs.next())
            {
                if(rbPoint.isSelected())
                {
                    buildingPoint.add(rs.getObject("bid"));
                }
                Object st = rs.getObject("shape");
                STRUCT strct = (STRUCT) st;
                JGeometry geoData = JGeometry.load(strct);
                
                double[] points = geoData.getOrdinatesArray();
            
               g.setColor(color);
                for (int i=0; i < points.length-3; i=i+2)
                {
                   
                  g.drawLine((int)points[i]+xOffset,(int)points[i+1]+yOffset,(int)points[i+2]+xOffset,(int)points[i+3]+yOffset);
                  
                  
                }
            }
            
            rs.close();
            
         
            
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e.toString());
        }
        
    }
    
        public void drawAnnouncementSystems(String query,Color color){
        try{
            
            DBConnection dbConn = new DBConnection();
            
            ResultSet rs = dbConn.getSpatialData(query);
             
            //output the query
            intNumQueries = intNumQueries + 1;
            txtAQueries.append("\n" +" Query:" + intNumQueries + " - " + query);
            
            Graphics g = this.getGraphics();
            
            while(rs.next())
            {
                if(rbPoint.isSelected())
                {
                    asPoint.add(rs.getObject("asid"));
                }
               
                int intRadius = rs.getInt(3);
                Object st = rs.getObject("circles");
                STRUCT strct = (STRUCT) st;
                JGeometry geoData = JGeometry.load(strct);
                
                if(rbEmergency.isSelected())
                {
                    strASValue = rs.getString("asid");
                }
                
                double[] points = geoData.getOrdinatesArray();
                
                if(rbStudent.isSelected() || rbEmergency.isSelected())
                {
                    String strHiddenPoints = "";
                    for(int i=0; i< points.length ;i++)
                    {
                        if(i == points.length - 1)
                        {
                            strHiddenPoints = strHiddenPoints + (int)points[i];
                        }
                        else
                        {
                            strHiddenPoints = strHiddenPoints + (int)points[i] + ",";
                        }
                    }
                    
                    lblHiddenAnValue.setText(strHiddenPoints);
                    
                }
                
                g.setColor(color);
                g.fillRect((int)points[0] + xOffset-7, (int)points[1]+yOffset-intRadius-7,15,15);
                g.drawOval((int)points[0] - intRadius + xOffset, (int)points[1] + yOffset- (2*intRadius), 2*intRadius, 2*intRadius);
                
            }
            
            rs.close();
            
         
            
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e.toString());
        }
        
    }
        
        public void drawStudents(String query,Color color){
        try{
            
            DBConnection dbConn = new DBConnection();
            
            
            
            ResultSet rs = dbConn.getSpatialData(query);
            
             //output the query
            intNumQueries = intNumQueries + 1;
            txtAQueries.append("\n" +" Query:" + intNumQueries + " - " + query);
            
            Graphics g = this.getGraphics();
            
            while(rs.next())
            {
                if(rbPoint.isSelected())
                {
                    studentPoint.add(rs.getObject("sid"));
                }
                Object st = rs.getObject("points");
                STRUCT strct = (STRUCT) st;
                JGeometry geoData = JGeometry.load(strct);
                
                double[] points = geoData.getFirstPoint();
                studentsForEmergency.add((int)points[0]);
                studentsForEmergency.add((int)points[1]);
                
                g.setColor(color);
                g.fillRect((int)points[0] + xOffset-5, (int)points[1]+yOffset-5,10,10);
                
                
                
            }
            
            rs.close();
            
         
            
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e.toString());
        }
        
    }
    
    

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnQuery;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JCheckBox cbAnnounce;
    private javax.swing.JCheckBox cbBuilding;
    private javax.swing.JCheckBox cbStudents;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCoordinates;
    private javax.swing.JLabel lblHiddenAnValue;
    private javax.swing.JLabel lblSelected;
    private javax.swing.JLabel lblSelectedValue;
    private javax.swing.JLabel lblValueCoordinates;
    private javax.swing.JRadioButton rbEmergency;
    private javax.swing.JRadioButton rbPoint;
    private javax.swing.JRadioButton rbRange;
    private javax.swing.JRadioButton rbStudent;
    private javax.swing.JRadioButton rbWhole;
    private javax.swing.JTextArea txtAQueries;
    // End of variables declaration                   

    @Override
    public void mousePressed(MouseEvent me) {
        
    }

    @Override
    public void mouseReleased(MouseEvent me) {
       
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

}
