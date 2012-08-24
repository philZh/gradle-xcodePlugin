package org.openbakery


import org.apache.commons.configuration.plist.XMLPropertyListConfiguration
import javax.imageio.ImageIO
import org.apache.commons.io.FileUtils
import java.awt.image.BufferedImage
import java.awt.RenderingHints
import org.gradle.api.tasks.TaskAction

/**
 * Created with IntelliJ IDEA.
 * User: rene
 * Date: 23.08.12
 * Time: 22:30
 * To change this template use File | Settings | File Templates.
 */
class HockeykitKitImageTask extends AbstractHockeykitTask {

    private static final int IMAGE_WIDTH = 114;

    def resizeImage(fromImage, toImage) {
        def image = ImageIO.read( new File(fromImage) )

        new BufferedImage( IMAGE_WIDTH, IMAGE_WIDTH, image.type ).with { i ->
            createGraphics().with {
                setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC )
                drawImage( image, 0, 0, HockeykitKitImageTask.IMAGE_WIDTH, HockeykitKitImageTask.IMAGE_WIDTH, null )
                dispose()
            }
            ImageIO.write( i, 'png', new File(toImage) )
        }
    }

    @TaskAction
    def imageCreate() {
        def infoplist = getInfoPlist()
        println infoplist
        XMLPropertyListConfiguration config = new XMLPropertyListConfiguration(new File(infoplist));
        def list = config.getList("CFBundleIconFiles");
        if (list.isEmpty()) {
            list = config.getList("CFBundleIcons.CFBundlePrimaryIcon.CFBundleIconFiles")
        }
        TreeMap<Integer, String> iconMap = new TreeMap<Integer, String>()
        list.each {
            item ->
            def image = ImageIO.read(new File(item));
            iconMap.put(image.width, item);
        }
        println "Images to choose from: " + iconMap;
        def outputDirectory = getOutputDirectory();

        def selectedImage = iconMap.get(114)

        def outputImageFile = new File(outputDirectory, "Icon.png");
        if (selectedImage != null) {
            println "Copy file " + selectedImage + " to " + outputImageFile;
            FileUtils.copyFile(new File(selectedImage), outputImageFile);
        } else {
            selectedImage = iconMap.lastEntry().value;
            println "Resize file " + selectedImage + " to " + outputImageFile;
            resizeImage(selectedImage, outputImageFile.absolutePath);
        }

    }

}
