import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import java.util.Scanner;


public class ApartatA {

    double generate_random_number(int min, int max) {
        double res = (Math.random() * ((max - min) + 1)) + min;  //min + static_cast <int> (rand()) /( static_cast <int> (RAND_MAX/(max-min)));
        return res;
    }

    void print_chart(double[] y, int Nreg) {
        System.out.println("NReg aqui es " + Nreg);
        double[] x = new double[Nreg];
        int i = 0;
        while (i < Nreg) {
            x[i] = (double)i;
            ++i;
        }
        LinearRegression lr = new LinearRegression(x,y);
        System.out.println(lr.toString());
        double xx = lr.slope();
        double yy = lr.intercept();

        XYChart chart = new XYChartBuilder().width(1600).height(1000).xAxisTitle("Regió").yAxisTitle("Posició de penetració").title("Comportament llum a través de N regions").build();
        XYStyler styler = chart.getStyler();
        styler.setXAxisMax((double)Nreg-1);
        styler.setYAxisMax((double)Nreg);
        styler.setYAxisMin(0.0);
        chart.addSeries("raig llum", x, y);
        /*y = new double[] {xx*x[0]+yy,xx*x[1]+yy,xx*x[2]+yy,xx*x[3]+yy,xx*x[4]+yy,xx*x[5]+yy,xx*x[6]+yy,xx*x[7]+yy,xx*x[8]+yy,xx*x[9]+yy};
        XYSeries series2 = chart.addSeries("LR", x, y);
        series2.setLineColor(XChartSeriesColors.YELLOW);*/

        i = 0;
        while (i < Nreg) {
            XYSeries series = chart.addSeries("Regió " + Integer.toString(i), new double[]{i, i}, new double[]{0,Nreg});
            series.setLineColor(XChartSeriesColors.RED);
            i++;
        }
        final SwingWrapper<XYChart> sw;
        sw = new SwingWrapper<>(chart);
        sw.displayChart();
    }

    void print_board(double v[], int N){
        System.out.print("Source");
        for (int i = 0; i < N-1; ++i) System.out.print("      ");
        for (int i = 0; i < N/2; ++i) System.out.print(" ");
        System.out.println("Detector");
	/*
	for (int i = 0; i < N/2; ++i) {
		for (int j = 0; j < N; ++j){
			System.out.print("   |   ";
		}
		System.out.print(endl;
	}
	*/
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                if (j < N) {
                    if (v[j] == i) System.out.print("   X   ");
                    else System.out.print("   |   ");
                }
                else System.out.print("   |   ");
            }
            System.out.println();
        }
	/*
	for (int i = 0; i < N/2; ++i){
		for (int j = 0; j < N; ++j){
			System.out.print("   |   ";
		}
		System.out.print(endl;
	}
	*/
    }

    int distance_between_two_points(int a, int b) {
        return Math.abs(a-b) + 1;
    }

    double snell(int m1, int m2, double envi1, double envi2) {
        double ca = 6;
        double co = (Math.abs(m1-m2))*1.9;
        double angleinc = Math.atan(co/ca) * 180 / Math.PI;
        System.out.println("The angle of incidence is: " + angleinc);
        double anglerefr = Math.sin(angleinc*Math.PI/180);
        anglerefr *= envi1;
        anglerefr /= envi2;
        anglerefr = Math.asin(anglerefr) * 180.0 / Math.PI;
        System.out.println("The angle of refraction is: " + anglerefr);
        return anglerefr;
    }

    double snell_with_incident_angle(double angleinc, double envi1, double envi2) {
        double anglerefr = Math.sin(angleinc*Math.PI/180);
        anglerefr *= envi1;
        anglerefr /= envi2;
        anglerefr = Math.asin(anglerefr) * 180.0 / Math.PI;
        System.out.println("The angle of refraction is: " + anglerefr);
        return anglerefr;
    }

    double src_to_dtct_angle(int m1, int m2, int Nreg){
        double ca = 7*(Nreg-1);
        double co = (Math.abs(m1-m2))*1.9;
        double angleinc = Math.atan(co/ca) * 180 / Math.PI;
        System.out.println("The angle of incidence from Source to Detector is: " + angleinc);
        return angleinc;
    }


    private double[] set_random_path(double[] v, int Nreg){
	/*
	v[0] = 0;
	v[Nreg-1] = Nreg-1;
	*/
        int randi = 0;
        for (int i = 0; i < Nreg; ++i) {
            randi = (int)generate_random_number(0, Nreg-1);
            v[i] = (double)randi;
            System.out.println("I'm position " + i + " and my value is " + randi);
        }
        return v;
    }

    private double[] recalculate_path(double[] v, int Nreg, double envi1, double envi2){
        double angleinc = src_to_dtct_angle((int)v[0], (int)v[Nreg-1], Nreg);
        double anglerefr = angleinc;
        for (int i = 0; i < Nreg; ++i) {
            if (i == Nreg/2-1) anglerefr = snell_with_incident_angle(angleinc, envi1, envi2);
            if (i == Nreg/2) anglerefr = src_to_dtct_angle((int)v[Nreg/2], (int)v[Nreg-1], Nreg/2);
            if (i+1 < Nreg) {
                double newvalue = (6*(Math.tan(anglerefr*Math.PI/180))/1.9);
                System.out.println("AUGMENT O DISMINUCIó DE " + newvalue);
                if (v[0] < v[Nreg-1]) v[i+1] = v[i] + newvalue;
                else v[i+1] = v[i] - newvalue;
            }
        }
        print_chart(v, Nreg);
        //print_board(v, Nreg);
        return v;
    }


    boolean try_random_y(double[] v, int Nreg){
        int dist = 0;
        for (int i = 0; i < Nreg-1; ++i) {
            dist += Math.abs(v[i]-v[i+1]);
            if (i == Nreg/2-1) snell((int)v[i], (int)v[i+1], 1.0, 1.0);
        }
        dist += Nreg;
        //System.out.print("Current distance: " << dist << endl;

        int randi = (int)generate_random_number(1, Nreg-2);
        double aux = v[randi];
        v[randi] = v[randi] + (int)generate_random_number(-((int)v[randi]), Nreg-(int)v[randi]);
        int newdist = 0;
        for (int i = 0; i < Nreg-1; ++i) {
            newdist += Math.abs(v[i]-v[i+1]);
        }
        newdist += Nreg;
        //System.out.print("New distance: " << newdist << endl;
        if (newdist <= dist) {
            print_board(v, Nreg);
            if (newdist == Nreg+Math.abs(v[0]-v[Nreg-1])) return true;
        }
        else {
            v[randi] = aux;
            return false;
        }
        return false;
    }

    /*void prettyize(int[] v, int Nreg) {
        int[] w = new int[] {0,1,2,3,4,5,6,7,8,9};
        Regression reg = new Regression(w , v);
        reg.lineal();
        System.out.println("y = " + reg.a + "x + " + reg.b);

    }*/

    void main(){
        System.out.print("Enter a natural number of regions: ");
        int Nreg = -1;
        Scanner reader = new Scanner(System.in);
        Nreg = reader.nextInt();

        System.out.println("Number of regions set to " + Nreg);
        System.out.println();

        double[] v = new double[Nreg];
        v = set_random_path(v, Nreg);
        print_board(v, Nreg);

        System.out.print("Do you want to set different environments (y/n)? ");
        String environ;
        environ = reader.next();
        System.out.println();
        double env1 = 1.0;
        double env2 = 1.0;
        if (environ.equals("y")) {
            System.out.println("Please choose the first environment (Air: type '1'; Glass: type '1,5'; Water: type '1,33')");
            env1 = reader.nextDouble();
            System.out.println("Please choose the second environment (Air: type '1'; Glass: type '1,5'; Water: type '1,33')");
            env2 = reader.nextDouble();
            v = recalculate_path(v, Nreg, env1, env2);
        }
        else{
            while (!try_random_y(v, Nreg));
            //prettyize(v, Nreg);
        }

    }

}
