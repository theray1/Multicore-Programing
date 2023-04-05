package fr.univnantes.multicore.distanciel;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class ThreadedServer implements Server {

    ExecutorService threadPool = Executors.newFixedThreadPool(10);

    private final ImageDrawer drawer;

    public ThreadedServer(ImageDrawer drawer) {
        this.drawer = drawer;
    }

    @Override
    public Server split(int blockSize) {
        Server base = this;

        //Implémentation de l'interface fonctionnelle Server, donc fonction getBlock()
        return largeArea -> {

            //No need for a synchronized list; all the future blocks are added sequentially
            List<Future<Block>> futureBlocks = new ArrayList<>();

            for(int i = largeArea.pixels().x; i < largeArea.pixels().getMaxX(); i+=blockSize) {
                for(int j = largeArea.pixels().y; j < largeArea.pixels().getMaxY(); j+=blockSize) {
                    var smallArea = largeArea.subPixels(i, j, blockSize, blockSize);
                    var futureBlock = threadPool.submit(new Callable<Block>() {
                        @Override
                        public Block call() throws Exception {
                            var returnBlock = base.getBlock(smallArea);
                            return returnBlock;
                        }
                    });

                    futureBlocks.add(futureBlock);
                }
            }

            //Implémentation de l'interface fonctionnelle Block, donc fonction draw()
            return (graphics) -> {
                int numberOfAvailableBlocks = 0;
                while (numberOfAvailableBlocks < futureBlocks.size()){
                    for(var block : futureBlocks) {
                        try {
                            if (block.isDone()) {
                                if (block.get().draw(graphics)) {
                                    numberOfAvailableBlocks++;
                                }
                            }

                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                return numberOfAvailableBlocks == futureBlocks.size();
            };
        };
    }


    //fonction appellée sur chaque sous-bloc
    @Override
    public Block getBlock(ScreenArea area) {
        Image image = drawer.getImage(area);
        return new ThreadedBlock(image, area);
    }
}

class ThreadedBlock implements Block {

    private final Image image;
    private final ScreenArea area;

    public ThreadedBlock(Image image, ScreenArea area) {
        this.image = image;
        this.area = area;
    }

    //fonction draw de chaque sous-bloc
    @Override
    public boolean draw(Graphics2D graphics) {
        return graphics.drawImage(image, area.pixels().x, area.pixels().y, null);
    }
}
