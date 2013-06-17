package org.ietr.preesm.experiment.ui.pimm.diagram;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

/**
 * Class used to provide icons used in the PiMM Diagram Editor
 * @author kdesnos
 *
 */
public class PiMMImageProvider extends AbstractImageProvider {

	 // The prefix for all identifiers of this image provider
    protected static final String PREFIX =
              "org.ietr.preesm.experiment.ui.pimm.";

 

    // The image identifier for white dot.
    public static final String IMG_WHITE_DOT_BLUE_LINE= PREFIX + "whitedotblueline";
    public static final String IMG_WHITE_DOT_GREY_LINE= PREFIX + "whitedotgreyline";
    public static final String IMG_PI= PREFIX + "pi";
	
	
	/**
	 * Default constructor of {@link PiMMImageProvider}.
	 */
	public PiMMImageProvider() {
		super();
	}

	@Override
	protected void addAvailableImages() {
        // register the path for each image identifier
        addImageFilePath(IMG_PI, "icons/pi.gif");
        addImageFilePath(IMG_WHITE_DOT_BLUE_LINE, "icons/whitedotblueline.gif");
        addImageFilePath(IMG_WHITE_DOT_GREY_LINE, "icons/whitedotgreyline.gif");
	}

}
