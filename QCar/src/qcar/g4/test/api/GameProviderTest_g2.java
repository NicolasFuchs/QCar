package qcar.g4.test.api;

import static org.junit.Assert.*;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

import qcar.ApiTest;
import qcar.IFactory;
import qcar.IGameDescription;
import qcar.IGameProvider;
import qcar.IQCar;
/**
 * Classe de test pour le GameProvider
 * @author Groupe 2 - QCar
 *
 */
public class GameProviderTest_g2 extends ApiTest{
   private static final double EPSILON = 1E-6;
   private IGameProvider gp;
   private final Random rdm;
   public GameProviderTest_g2(IFactory fact, IFactory aux) {
      super(fact, aux);
      this.rdm = new Random();
   }
   /**
    * Teste que le gameprovider ne doit jamais rendre de configuration de jeu nul
    */
   @Test
   public void testGPReturnNotNullGameDescription(){
     int nSteps = 50;
     int numberOfStyles = this.auxiliaryFactory.numberOfStyles();
     for(int i = 0; i<numberOfStyles; i++){
       this.gp = this.factoryUnderTest.newGameProvider(i);
       for(int j = 0; j<nSteps; j++){
         assertNotNull(this.gp.nextGame(1+this.rdm.nextInt(50)));
       }
     }
   }
   /**
    * Teste que les qcars ne s'interesctent pas dans une configuration initial du jeu. 
    * R�alis� pour chaque style existant.
    */
//   @Test
//   public void testIfNoIntersect(){
//     int nSteps = 50;
//     int numberOfStyles = this.auxiliaryFactory.numberOfStyles();
//     for(int i = 0; i<numberOfStyles; i++){
//       this.gp = this.factoryUnderTest.newGameProvider(i);
//       for(int j = 0; j<nSteps; j++){
//         ArrayList<Area> areas = new ArrayList<>();
//         int numberOfDrivers = 1+this.rdm.nextInt(50);
//         IGameDescription gd = this.gp.nextGame(numberOfDrivers);
//         for(IQCar qcar : gd.allQCar()){
//            Area area = createAreaFromQCar(qcar);
//            for(Area existingArea : areas){
//              Area test = new Area(area);
//              test.intersect(existingArea);
//              assertTrue(test.isEmpty());
//            }
//            areas.add(area);
//         }
//       }
//     }
//   }
   
   /**
    * Teste que chaque qcar respecte sa surface minimum pour la configuration initial du jeu.
    * Realis� pour chaque style existant.
    */
//   @Test
//   public void testMinSurface(){
//     int nSteps = 50;
//     int numberOfStyles = this.auxiliaryFactory.numberOfStyles();
//     for(int i = 0; i<numberOfStyles; i++){
//       this.gp = this.factoryUnderTest.newGameProvider(i);
//       for(int j = 0; j<nSteps; j++){
//         int numberOfDrivers = 1+this.rdm.nextInt(50);
//         IGameDescription gd = this.gp.nextGame(numberOfDrivers);
//         for(IQCar qcar : gd.allQCar()){
//            double minArea=qcar.nature().minArea();
//            minArea *= 1-EPSILON;
//            double a=getArea(qcar);
//            String s=qcar.nature().toString();
//            assertTrue(s+" "+a+" "+minArea, a >= minArea);
//         }
//       }
//     }
//   }
   
   /**
    * M�thode qui contr�le que les c�t�s soient plus petit que la grandeur max.
    */
   @Test
   public void testMaxSideLength(){
     int nSteps = 50;
     int numberOfStyles = this.auxiliaryFactory.numberOfStyles();
     for(int i = 0; i<numberOfStyles; i++){
       this.gp = this.factoryUnderTest.newGameProvider(i);
       for(int j = 0; j<nSteps; j++){
         int numberOfDrivers = 1+this.rdm.nextInt(50);
         IGameDescription gd = this.gp.nextGame(numberOfDrivers);
         for(IQCar qcar : gd.allQCar()){
            Point2D vertex0 = qcar.vertex(0);
            Point2D vertex1 = qcar.vertex(1);
            Point2D vertex2 = qcar.vertex(2);
            Point2D vertex3 = qcar.vertex(3);
            double maxSideLength = qcar.nature().maxSideLength();
            maxSideLength *= 1+EPSILON; // epsilon tolerance...
//            System.out.println("------------");
//            System.out.println(maxSideLength);
//            System.out.println(getDistanceBetweenTwoPoints(vertex1,vertex2));
            assertTrue(getDistanceBetweenTwoPoints(vertex0,vertex1) <= maxSideLength);
            assertTrue(getDistanceBetweenTwoPoints(vertex1,vertex2) <= maxSideLength);
            assertTrue(getDistanceBetweenTwoPoints(vertex2,vertex3) <= maxSideLength);
            assertTrue(getDistanceBetweenTwoPoints(vertex3,vertex0) <= maxSideLength);
         }
       }
     }
   }
   
   /**
    * Calcule la distance entre deux points
    * @param point1
    * @param point2
    * @return distance entre les deux points
    */
   private double getDistanceBetweenTwoPoints(Point2D point1, Point2D point2) {
     return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2)
         + Math.pow(point1.getY() - point2.getY(), 2));
   }
   
   /**  ????????????????????????????
    * M�thode qui calcule la surface d'un qcar
    * @param qcar dont on va calculer la surface
    * @return la surface du qcar
    */
//   private double getArea(IQCar qcar) {
//     return Math.abs((qcar.vertex(3).getY()-qcar.vertex(0).getY())*(qcar.vertex(1).getX()-qcar.vertex(0).getX()));
//   }
   /**
    * M�thode qui cr�e un object de type Area d'un qcar
    * @param qcar dont on va retourner son Area
    * @return Area correspondant au qcar en param.
    */
   private static Area createAreaFromQCar(IQCar qcar){
     Path2D path2 = new Path2D.Double();
     Point2D vertex = qcar.vertex(0);
     path2.moveTo(vertex.getX(), vertex.getY());
     for (int i = 1; i < 4; i++) {
       vertex = qcar.vertex(i);
       path2.lineTo(vertex.getX(), vertex.getY());

     }
     path2.closePath();
     return new Area(path2);
  }

}
