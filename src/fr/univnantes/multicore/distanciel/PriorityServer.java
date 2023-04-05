package fr.univnantes.multicore.distanciel;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class PriorityServer implements Server {

    PriorityThreadPool priorityThreadPool = new PriorityThreadPool(20);

    private final ImageDrawer drawer;

    public PriorityServer(ImageDrawer drawer) {
        this.drawer = drawer;
    }

    @Override
    public Server split(int blockSize) {
        Server base = this;

        //Implémentation de l'interface fonctionnelle Server, donc fonction getBlock()
        return largeArea -> {

            //Need a synchronized list; all the computed blocks are added in parallel by the threads that computed them
            SynchronizedList<Block> computedBlockList = new SynchronizedList<Block>();
            int nbBlocks = 0;

            for(int i = largeArea.pixels().x; i < largeArea.pixels().getMaxX(); i+=blockSize) {
                for(int j = largeArea.pixels().y; j < largeArea.pixels().getMaxY(); j+=blockSize) {
                    var smallArea = largeArea.subPixels(i, j, blockSize, blockSize);
                    boolean hasPriority = drawer.hasPriority(smallArea);
                    priorityThreadPool.execute(new Runnable(){
                        @Override
                        public void run() {
                            Block returnBlock = base.getBlock(smallArea);
                            computedBlockList.add(returnBlock);
                        }
                    }, hasPriority);
                    nbBlocks++;
                }
            }



            //Implémentation de l'interface fonctionnelle Block, donc fonction draw()
            int finalNbBlocks = nbBlocks;
            return (graphics) -> {

                int size = computedBlockList.size();
                int numberOfBlocksDrawn = 0;


                while (numberOfBlocksDrawn < finalNbBlocks){
                    for(int i = 0; i < size; i++){
                        try {
                            computedBlockList.get(i).draw(graphics);
                            numberOfBlocksDrawn++;
                        } catch (Exception e) {

                        }
                    }
                }

                var isMandelbrotCompleted = numberOfBlocksDrawn == finalNbBlocks;

                if(isMandelbrotCompleted){
                    priorityThreadPool.waitUntilAllTasksFinished();
                    priorityThreadPool.stop();
                }

                return isMandelbrotCompleted;
            };
        };
    }


    //fonction appellée sur chaque sous-bloc
    @Override
    public Block getBlock(ScreenArea area) {
        Image image = drawer.getImage(area);
        return new PriorityBlock(image, area);
    }
}

class PriorityBlock implements Block {

    private final Image image;
    private final ScreenArea area;

    public PriorityBlock(Image image, ScreenArea area) {
        this.image = image;
        this.area = area;
    }

    //fonction draw de chaque sous-bloc
    @Override
    public boolean draw(Graphics2D graphics) {
        boolean draw = graphics.drawImage(image, area.pixels().x, area.pixels().y, null);
        return draw;
    }
}
