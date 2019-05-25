import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.colors.XChartSeriesColors;

import java.util.Scanner;

class ApartatB {
    private final double indexFibra = 1.47;
    private int regio = 0;
    private int nreg;
    private double angleF = 0;
    private boolean up = true;
    private double posX;
    private double posY;
    private int alpha;
    private int pointsxU;

    ApartatB() {}

    void main() throws Exception {
        Scanner reader = new Scanner(System.in);
        System.out.print("Enter Natural Number of Regions (from 20 to 50): ");
        nreg = reader.nextInt();
        if (nreg > 50 || nreg < 20) throw new Exception("input error number of regions");
        double angle;
        System.out.print("Set initial angle (sexagesimal from 0 to 90): ");
        angle = reader.nextDouble();
        System.out.print("Set alpha(Natural Number): ");
        alpha = reader.nextInt();
        System.out.print("Selecciona el mode (0,1): ");
        int mode = reader.nextInt();
        if (mode == 0) {
            System.out.print("How much points/update(Natural Number): ");
            pointsxU = reader.nextInt();
        }
        reader.close();
        if (angle > 90 || angle < 0) throw new Exception("input error angle");

        posY = nreg/2;
        posX = 0;
        double indexAire = 1;
        angle = snell(indexAire, angle, indexFibra);
        angleF = 90-angle;
        //System.out.println(angleF);

        double[][] initdata = new double[][] {{-2, 0, Math.tan(Math.PI * angleF / 180)}, {posY-2*Math.tan(angle), posY, posY+1}};
        XYChart chart2 = new XYChartBuilder().width(1300).height(600).xAxisTitle("length").yAxisTitle("Regió").title("Comportament raig llum FO GRIN").build();
        // Show it
        XYStyler styler = chart2.getStyler();
        styler.setYAxisMax((double) nreg);
        styler.setYAxisMin(0.0);
        chart2.addSeries("raig llum", initdata[0], initdata[1]);

        XYSeries series = chart2.addSeries("entrada fibra", new double[]{0, 0}, new double[]{50, 0});
        series.setLineColor(XChartSeriesColors.RED);

        double[] superior = new double[] {(double) nreg, (double) nreg};
        double[] inferior = new double[] {0.0, 0.0};
        double[] contador = new double[] {-100.0,100000.0};

        XYSeries series2 = chart2.addSeries("recobriment superior", contador, superior);
        series2.setLineColor(XChartSeriesColors.BLACK);
        XYSeries series3 = chart2.addSeries("recobriment inferior", contador, inferior);
        series3.setLineColor(XChartSeriesColors.BLACK);

        styler.setXAxisMin(-5.0);
        styler.setXAxisMax(10.0);

        final SwingWrapper<XYChart> sw = new SwingWrapper<>(chart2);
        sw.displayChart();
        Thread.sleep(1000);
        if (mode == 0) {
            for (int i = 0; i < 50; ++i) {
                Thread.sleep(5000);
                double[][] data = getData(mode);
                styler.setXAxisMin(data[0][0]);
                styler.setXAxisMax(data[0][pointsxU-1]);
                chart2.updateXYSeries("raig llum", data[0], data[1], null);
                sw.repaintChart();
            }
        }
        else {
            double xmin = 0;
            double[][] data = getData(mode);
            //System.out.println(data[0].length);
            chart2.updateXYSeries("raig llum", data[0], data[1], null);
            Thread.sleep(1000);
            for (int i = 0; i < 50; ++i) {
                Thread.sleep(2000);
                styler.setXAxisMin(xmin);
                styler.setXAxisMax(xmin+nreg);
                sw.repaintChart();
                xmin += nreg/2;
            }
        }
    }

    private double[][] getData(int mode) {
        double[] xData;
        double[] yData;
        if (mode == 0) {
            xData = new double[pointsxU];
            yData = new double[pointsxU];
        }
        else {
            xData = new double[100000];
            yData = new double[100000];
        }

        for (int i = 0; i < xData.length; ++i) {
            double indexActualRegio = calculIndexRegio(regio, nreg / 2, alpha);
            if (up) {
                Double snell = snell(indexActualRegio, angleF, calculIndexRegio(regio + 1, nreg / 2, alpha));
                Double angleC = angleCritic(indexActualRegio, calculIndexRegio(regio + 1, nreg / 2, alpha));
                //System.out.println("snellu = " + snell);
                //System.out.println("criticu = " + angleC);
                if (angleC < snell) up = !up;
                else angleF = snell;
                if (posY != nreg) upThings(xData, yData, i);
                else {
                    up = false;
                    downThings(xData, yData, i);
                }
            }
            else {
                Double snell = snell(indexActualRegio, angleF, calculIndexRegio(regio - 1, nreg / 2, alpha));
                Double angleC = angleCritic(indexActualRegio, calculIndexRegio(regio - 1, nreg / 2, alpha));
                //System.out.println("snelld = " + snell);
                //System.out.println("criticd = " + angleC);
                if (angleC < snell) up = !up;
                else angleF = snell;
                if (posY != 0) downThings(xData, yData, i);
                else {
                    up = true;
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

    }

    private void upThings(double[] xData, double[] yData, int i) {
        yData[i] = posY + 1;
        xData[i] = posX + (Math.tan(Math.PI * angleF / 180));
        regio++;
        posY++;
        posX = posX + (Math.tan(Math.PI * angleF / 180));
    }

    private double calculIndexRegio(double r, double a, int alfa) {
        double delta = 0.01;
        return indexFibra*Math.sqrt(1-2*delta*Math.pow((Math.abs(r)/a),alfa));
    }

    private double snell(double n1, double angleIncident, double n2) {
        //System.out.println("n1 = " + n1 + ", angI = " + angleIncident + ", n2 = " + n2);
        return Math.asin(n1*Math.sin(Math.PI*angleIncident/180)/n2)*180/Math.PI;
    }

    private double angleCritic(double n1, double n2) {
        if (n1 < n2) return Math.asin(n1/n2)*180/Math.PI;
        return Math.asin(n2/n1)*180/Math.PI;
    }
}
