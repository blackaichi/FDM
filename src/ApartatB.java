import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.util.Scanner;

public class ApartatB {
    private final double indexFibra = 1.47;
    private int regio = 0;
    private int nreg;
    private double angleF = 0;
    private boolean up = true;
    private double posX;
    private double posY;
    private int alpha;

    ApartatB() {}

    public void main() throws Exception {
        Scanner reader = new Scanner(System.in);
        System.out.print("Enter Natural Number of Regions: ");
        nreg = reader.nextInt();
        //if (nreg > 50 || nreg < 20) throw new Exception("input error number of regions");
        double angle;
        System.out.print("Set initial angle (sexagesimal from 0 to 90): ");
        angle = reader.nextDouble();
        System.out.print("Set alpha(Natural Number): ");
        alpha = reader.nextInt();
        reader.close();
        if (angle > 90 || angle < 0) throw new Exception("input error angle");

        posY = nreg/2;
        posX = 0;
        double indexAire = 1;
        angle = snell(indexAire, angle, indexFibra);
        angleF = 90-angle;
        System.out.println(angleF);

        double[][] initdata = new double[][] {{-1, 0}, {posY-Math.tan(angle), posY}};
        XYChart chart2 = new XYChartBuilder().width(1300).height(600).xAxisTitle("length").yAxisTitle("Regió").title("Comportament raig llum FO GRIN").build();
        // Show it
        chart2.addSeries("raig llum", initdata[0], initdata[1]);
        final SwingWrapper<XYChart> sw = new SwingWrapper<>(chart2);
        sw.displayChart();

        while (true) {
            Thread.sleep(3000);
            double[][] data = getData();
            chart2.updateXYSeries("raig llum", data[0], data[1], null);
            sw.repaintChart();
            for (double[] da : data) {
                for (double d : da) {
                    System.out.print(d + " ");
                }
                System.out.println();
            }
        }
    }

    private double[][] getData() {

        double[] xData = new double[20];
        double[] yData = new double[20];

        for (int i = 0; i < xData.length; ++i) {
            double indexActualRegio;
            if (up) {
                if (posY+1 != nreg) upThings(xData, yData, i);
                else {
                    up = !up;
                    downThings(xData, yData, i);
                }
            }
            else {
                if (posY-1 != -1) downThings(xData, yData, i);
                else {
                    up = !up;
                    upThings(xData, yData, i);
                }
            }
        }
        return new double[][] { xData, yData };
    }

    private void downThings(double[] xData, double[] yData, int i) {
        yData[i] = posY - 1;
        xData[i] = posX + (Math.tan(Math.PI * angleF / 180));
        regio--;
        posY--;
        posX = posX + (Math.tan(Math.PI * angleF / 180));
        double indexActualRegio = calculIndexRegio(regio, nreg / 2, alpha);
        Double snell = snell(indexActualRegio, angleF, calculIndexRegio(regio - 1, nreg / 2, alpha));
        System.out.println("snelld = " + snell);
        Double angleC = angleCritic(indexActualRegio, calculIndexRegio(regio - 1, nreg / 2, alpha));
        System.out.println("criticd = " + angleC);
        if (angleC < snell) up = !up;
        else angleF = snell;
    }

    private void upThings(double[] xData, double[] yData, int i) {
        yData[i] = posY + 1;
        xData[i] = posX + (Math.tan(Math.PI * angleF / 180));
        regio++;
        posY++;
        posX = posX + (Math.tan(Math.PI * angleF / 180));
        double indexActualRegio = calculIndexRegio(regio, nreg / 2, 1);
        Double snell = snell(indexActualRegio, angleF, calculIndexRegio(regio + 1, nreg / 2, 1));
        Double angleC = angleCritic(indexActualRegio, calculIndexRegio(regio - 1, nreg / 2, 1));
        System.out.println("snellu = " + snell);
        System.out.println("criticu = " + angleC);
        if (angleC < snell) up = !up;
        else angleF = snell;
    }

    private double calculIndexRegio(double r, double a, int alfa) {
        double delta = 0.01;
        return indexFibra*Math.sqrt(1-2* delta *Math.pow((Math.abs(r)/a),alfa));
    }

    private double snell(double n1, double angleIncident, double n2) {
        System.out.println("n1 = " + n1 + ", angI = " + angleIncident + ", n2 = " + n2);
        return Math.asin(n1*Math.sin(Math.PI*angleIncident/180)/n2)*180/Math.PI;
    }

    private double angleCritic(double n1, double n2) {
        if (n1 < n2) return Math.asin(n1/n2)*180/Math.PI;
        return Math.asin(n2/n1)*180/Math.PI;
    }
}
