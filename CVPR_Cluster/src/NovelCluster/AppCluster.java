package NovelCluster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;

public class AppCluster {
    static int[] colorData;
    static int[] markPoint;
    static int cc=0;
    static double[] radius;
    static int copy =0;
    public static ArrayList<String[]> wordsArray = new ArrayList<String[]>();
    static ArrayList<int[]> clusterArray = new ArrayList<int[]>();
    
    //Function for Checkpointing the data point are done or not
    static int checkIfDone(int[] colordata,int COUNT){
        for(int i=0; i<COUNT; i++){
            if(colordata[i]==0){
                //System.out.println(colordata);
                return 0;
            }
        }
        return 1;
    }
    
    // function for rounding the value
    public static double round(double d, int decimalPlace) {
        double faltu = Math.round(d*Math.pow(10,decimalPlace))/(Math.pow(10,decimalPlace));
        return faltu;
    }
    // Main Method
    public static void main(String[] args) {
        try{
        	System.out.println("I am Here");
            BufferedReader buf = new BufferedReader(new FileReader("/home/abose/TestCluster/CVPR_Cluster/aggregation.txt"));
            ArrayList<int[]> linkedComponent = new ArrayList<int[]>();
            String lineJustFetched = null;
            double ratioRange;
            double deltaRadius = 0.001;
            double dst_ij;
            double rad_ij;
            int flag= 0;
            double m =0;
            Scanner scan = new Scanner(System.in);//Range Ratio Input Taking
            ratioRange=scan.nextDouble();
            scan.close();
            
            // DATASET data insertion in while loop
            while(true){
                lineJustFetched = buf.readLine();
                //System.out.println(lineJustFetched);
                if(lineJustFetched == null){  
                    break; 
                }
                else{
                    String[] words = lineJustFetched.split("\t");
                    //System.out.println(words[0]+ "  "+words[1] + "  "+words[2]);
                    wordsArray.add(words); 
                }
            }
            
            //Size declaration of colorData and radius 1D array
            colorData = new int[wordsArray.size()];
            radius = new double[wordsArray.size()];
            markPoint= new int[wordsArray.size()];
            //This for loop for Initializing the Array
            for(int i=0; i<wordsArray.size(); i++)
            {
                radius[i] = 0.01;
                colorData[i] = 0;
            }
            
            // This While loop for Making Relation Between two Datapoint
            while(checkIfDone(colorData, wordsArray.size())!= 1){
                
                //For Loop for Incrementing Radius
                for(int i=0; i<wordsArray.size(); i++){
                    if(colorData[i] == 0){
                        radius[i] = radius[i] + deltaRadius;
                    }
                }
                
                //For Loop for Getting Linked Components
                for(int i=0; i<wordsArray.size(); i++){
                    if(colorData[i]==0){
                        //System.out.println("BAL1->"+wordsArray.get(i)[0]+ "  BAL2->"+ wordsArray.get(i)[1]+ "  radius["+i+"]->" + radius[i]);
                        if((radius[i]>= (1+ratioRange)*m) && flag==1)
                        {
                        	System.out.println("value of m->" + m);
                            m=0;
                            flag = 0;
                            ConnectedComponents concomp = new ConnectedComponents(linkedComponent);
                            clusterArray.addAll(concomp.funcCall());
                            System.out.println("cltsz->"+ clusterArray.size());
                            linkedComponent.clear();
                        }
                        for(int j=0; j<wordsArray.size(); j++){
                            String bal1 = wordsArray.get(i)[0];
                            String bal2 = wordsArray.get(i)[1];
                            String chal1 = wordsArray.get(j)[0];
                            String chal2 = wordsArray.get(j)[1];
                            double BAL1 = Double.parseDouble(bal1);
                            double BAL2 = Double.parseDouble(bal2);
                            double CHAL1 = Double.parseDouble(chal1);
                            double CHAL2 = Double.parseDouble(chal2);
                            //System.out.println("BAL1->"+BAL1+ "  BAL2->"+ BAL2 + "  CHAL1->"+ CHAL1+ "  CHAL2->" + CHAL2 + "radius["+i+"]->" + radius[i]);
                            double temp1 = Math.sqrt(Math.pow((BAL1-CHAL1),2)) + Math.sqrt(Math.pow((BAL2-CHAL2),2));
                            //System.out.println("temp1-> "+ temp1);
                            dst_ij    = round(temp1,2);
                            double temp2 = (radius[i]+ radius[j]);
                            rad_ij = round(temp2, 2);
                            if(dst_ij == rad_ij && dst_ij!=0 && rad_ij!= 0)
                            {
                                copy++;
                                //System.out.println("dst_ij->"+ dst_ij +" rad_ij->"+ rad_ij+ " r_i-> "+ radius[i]+  " r_j-> "+ radius[j]+" copy->"+copy + " i->" + i +" j->"+ j);
                                if(colorData[j]==0) {
                                	colorData[i] = 1;
                                    colorData[j] = 1;
                                    linkedComponent.add( new int[]{i,j});
                                }
                                else
                                {
                                	if(markPoint[j]==0) {
                                	colorData[i]=1;
                                	linkedComponent.add( new int[]{i,j});
                                	}
                                	else
                                		continue;
                                		//linkedComponent.add(new int[]{i,-1});
                                }
                                if(flag==0){
                                    m= radius[i];
                                    System.out.println("value of m <-" + m);
                                    flag = 1;
                                    System.out.println("aschi");
                                }
                            }
                        }
                    }
                }//For Loop ends here
            }//While Loop ends here
            ConnectedComponents concomp = new ConnectedComponents(linkedComponent);
            clusterArray.addAll(concomp.funcCall());
            FileWriter writer = new FileWriter("/home/abose/TestCluster/CVPR_Cluster/output.txt");
 
            for (int [] arr : clusterArray){
                System.out.print(""+arr[0] + "  "+arr[1]);
                System.out.println();
                writer.write(arr[0]+"\t"+ arr[1]);
                writer.write(System.lineSeparator());
            }
            writer.close();
            System.out.println("clustArrsize "+ clusterArray.size());
            //for(int k=0; k<clusterArray.size() ;k++)
            //System.out.println("clustArrsize "+ clusterArray.size()+ " hehe-> " + clusterArray.get(k)[0]+ " " + clusterArray.get(k)[1]);
            
            buf.close();
        }catch(Exception e){
            e.printStackTrace();
        } 
    }
}    
// Sample program to find connected components of undirected graph

 
class CCGraph
{    
    static int MAXInit;
    static ArrayList<int[]> clustRepRef;
    public Integer[][]       edges;
    public int[]       degree;

    CCGraph(ArrayList<int[]> clustRepRec)
    {
    	
        MAXInit= AppCluster.wordsArray.size();//correction
        clustRepRef= clustRepRec;
        edges = new Integer[MAXInit + 1][MAXInit];
        degree = new int[MAXInit + 1];
        for(int z=0;z<clustRepRef.size(); z++)
            //System.out.println(clustRepRef.get(z)[0]+ " "+ clustRepRef.get(z)[1]+ "  MAXInit-> " + MAXInit);
        for (int i = 1; i <= MAXInit; i++)
            degree[i] = 0;
    }
    
    void read_CCGraph(boolean directed)
    {
        int x, y;

       for  (int i = 0; i < clustRepRef.size(); i++)
        {
            x = clustRepRef.get(i)[0];
            y = clustRepRef.get(i)[1];
            System.out.println("x->"+ x+ "  y->" + y + "  Bool->" + directed);
            insert_edge(x, y, directed);
        }

        clustRepRef.clear();
    }

    void insert_edge(int x, int y, boolean directed)
    {
        if (degree[x] > MAXInit)
            System.out.println("Warning: insertion (%d, %d) exceeds max degree\n"+  x + y);

        edges[x][degree[x]] = y;
        //System.out.println("degree["+ x + "] "+ degree[x]);
        degree[x]++; 
        //System.out.println("degree2["+ x + "] "+ degree[x]);
        
        if (!directed)
            insert_edge(y, x, true);
    } 

    void print_CCGraph()
    {
        for (int i = 1; i <= MAXInit; i++)
        {
        	if(edges[i][0]!=null)
        	{
	            System.out.print(i+ "->");
	            for (int j = degree[i] - 1; j >= 0; j--)
	            {
	                System.out.print(" "+edges[i][j] + " ");
	            }
	            System.out.println();
        	}
        }
    }
}

class ConnectedComponents
{
    static int MAXV;
    static ArrayList<Integer> receiver = new ArrayList<>();
    static ArrayList<int[]> clusterSave = new ArrayList<int[]>();
    static boolean   processed[];
    static boolean   discovered[];
    static int       parent[];
    static ArrayList<int[]> clustRep;

    ConnectedComponents(ArrayList<int[]> clustRepCopy1)
    {
        MAXV = AppCluster.wordsArray.size() ;
        processed  = new boolean[MAXV];
        discovered = new boolean[MAXV];
        parent     = new int[MAXV];
       clustRep = clustRepCopy1;
       //for(int z=0;z<clustRep.size(); z++)
           //System.out.println(clustRep.get(z)[0]+ " "+ clustRep.get(z)[1]);
    }
    
    static ArrayList<Integer> bfs(CCGraph g, int start)
    {
        Queue<Integer> q = new LinkedList<Integer>();
        int i, v;
        ArrayList<Integer> component = new ArrayList<>();
        //System.out.println("start" + start);
        q.offer(start);

        discovered[start] = true;

        while (!q.isEmpty())
        {
            v = q.remove();
            component.add(v);
            AppCluster.markPoint[v]=1;
            //System.out.println(v);
            //process_vertex(v);
            processed[v] = true;

            for (i = g.degree[v] - 1; i >= 0; i--)
            {
                //System.out.println("Value of i " + i);
                if (!discovered[g.edges[v][i]])
                {
                    q.offer(g.edges[v][i]);
                    discovered[g.edges[v][i]] = true;
                    parent[g.edges[v][i]] = v;
                }
            }
        }
        return component;
    }

    static ArrayList<int[]> connected_components(CCGraph gCln)
    {
        clusterSave.clear();
        //System.out.println("vertices -> " + g.nvertices);
        System.out.println();
        for (int i = 1; i <= CCGraph.MAXInit; i++)
        {
        	if(gCln.edges[i][0]!=null)
            {
            	if (!discovered[i])
	        	{
            		AppCluster.cc++;
	                //System.out.println("Hudai" + AppCluster.cc);
	                receiver.addAll(bfs(gCln, i));
	                for(int k= 0; k< receiver.size(); k++){
	                	clusterSave.add(new int[] {receiver.get(k), AppCluster.cc});
	                	System.out.println("Test clusterSave Size->" + clusterSave.size());
	                }
	                receiver.clear();
	                System.out.println();
	        	}
            }
        }
        return clusterSave;
    }

    ArrayList<int[]> funcCall()
    {
        CCGraph g = new CCGraph(clustRep);

        g.read_CCGraph(false);
        
        g.print_CCGraph();

        return connected_components(g);
    }
}