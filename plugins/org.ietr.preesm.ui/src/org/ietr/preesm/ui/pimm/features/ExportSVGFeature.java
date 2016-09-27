package org.ietr.preesm.ui.pimm.features;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;
import org.ietr.preesm.ui.pimm.util.PiMMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportSVGFeature extends AbstractCustomFeature {
	protected IFeatureProvider fp;
	
	Font getFont(AbstractVertex aa){
		// Retrieve the shape and the graphic algorithm
		PictogramElement[] actorPes = fp.getAllPictogramElementsForBusinessObject(aa);
		
		ContainerShape containerShape = null;
		for(PictogramElement pe : actorPes){
			if(pe instanceof ContainerShape)
				containerShape = (ContainerShape)pe;
		}
		if(containerShape == null)
			throw new IllegalArgumentException("getFont of a AbstractVertex without ContainerShape");
		
		EList<Shape> childrenShapes = containerShape.getChildren();
				
		for (Shape shape : childrenShapes) {
			GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
			// The name should be the only children with type text
			if (child instanceof Text) {
				return ((Text) child).getFont();
			}
		}
		return null;
	}
	
	Set<FreeFormConnection> getFifoFFC(Fifo f){
		Set<FreeFormConnection> result = new HashSet<FreeFormConnection>();
		PictogramElement[] pes = fp.getAllPictogramElementsForBusinessObject(f);
		if (pes == null) return null;
		
		for(int i=0; i<pes.length; i++){
			Fifo fPe = (Fifo)(((pes[i]).getLink().getBusinessObjects()).get(0));
			if(fPe.equals(f)){
				result.add((FreeFormConnection) pes[i]);
			}
		}
		return result;
	}
	
	FreeFormConnection getDepFFC(Dependency d){
		PictogramElement[] pes = fp.getAllPictogramElementsForBusinessObject(d);
		if (pes == null) return null;
		
		int id = -1;
		for(int i1=0; i1<pes.length; i1++){
			Dependency dPe = (Dependency)(((pes[i1]).getLink().getBusinessObjects()).get(0));
			if(dPe.equals(d))
				id = i1;
		}
		
		return (FreeFormConnection) pes[id];
	}
	
	BoxRelativeAnchor getPortBra(Port p){
		PictogramElement[] pes = fp.getAllPictogramElementsForBusinessObject(p);
		if (pes == null) return null;
		
		int id = -1;
		for(int i1=0; i1<pes.length; i1++){
			Port pPe = (Port)(((pes[i1]).getLink().getBusinessObjects()).get(0));
			if(pPe.equals(p))
				id = i1;
		}
		
		return (BoxRelativeAnchor) pes[id];
	}
	
	void configTextToSVG(Element el, Text t){
		int textHeight = GraphitiUi.getUiLayoutService()
				.calculateTextSize(t.getValue(), t.getFont()).getHeight();
		switch(t.getVerticalAlignment()){
		case ALIGNMENT_BOTTOM:
	        el.setAttribute("y", ""+(t.getY()+t.getHeight()));
			break;
		case ALIGNMENT_CENTER:
	        el.setAttribute("y", ""+(t.getY()+t.getHeight()/2+t.getFont().getSize()/2-2));
			break;
		case ALIGNMENT_TOP:
	        el.setAttribute("y", ""+(t.getY()+textHeight));
		default:
			break;		
		}
		
		switch(t.getHorizontalAlignment()){
		case ALIGNMENT_LEFT:
			el.setAttribute("x", "" + (-10));// TODO (t.getX()-t.getWidth()));
	        el.setAttribute("text-anchor", "end");
			break;
		case ALIGNMENT_RIGHT:
			el.setAttribute("x", ""+(t.getX()+2));
	        el.setAttribute("text-anchor", "start");
			break;
		default:
		case ALIGNMENT_MIDDLE:
			el.setAttribute("x", ""+(t.getX()+t.getWidth()/2));
	        el.setAttribute("text-anchor", "middle");
			break;		
		}

        el.setAttribute("fill", "rgb(" +
        		t.getForeground().getRed() + ", " +
        		t.getForeground().getGreen() + ", " +
        		t.getForeground().getBlue() + ")");
        
		el.setAttribute("font-size", t.getFont().getSize() + "pt");	
        el.setAttribute("font-family", t.getFont().getName());		
        if(t.getFont().isBold())
            el.setAttribute("font-weight", "bold");		
        if(t.getFont().isItalic())
            el.setAttribute("font-style", "italic");       
		
	}
	
	void addFontToSVG(Element e, Font f){
		if(f == null) return;
		
        e.setAttribute("font-size", f.getSize() + "pt");	
        e.setAttribute("font-family", f.getName());		
        if(f.isBold())
            e.setAttribute("font-weight", "bold");		
        if(f.isItalic())
            e.setAttribute("font-style", "italic");		        	
	}
		
	protected static int computeActorHeight(ExecutableActor ea){
		int height;
		
		/* Compute Actor Height */
		int nConfigPorts = java.lang.Math.max(ea.getConfigInputPorts().size(), ea.getConfigOutputPorts().size());
		int nDataPorts = java.lang.Math.max(ea.getDataInputPorts().size(), ea.getDataOutputPorts().size());
		height = 25 /* Name */
				+ nConfigPorts * 15
				+ nDataPorts * 15;
		
		return height;
	}
	
	public ExportSVGFeature(IFeatureProvider fp) {
		super(fp);
		this.fp = fp;
	}
	
	@Override
	public String getName() {
		return "Export to SVG";
	}

	@Override
	public String getDescription() {
		return "Export Graph to a SVG image file.";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}
	
	public boolean hasDoneChanges(){
		return false;
	}
	
	@Override
	public void execute(ICustomContext context) {
		/* Get PiGraph */
		PictogramElement[] pes = context.getPictogramElements();
		Object bo = getBusinessObjectForPictogramElement(pes[0]);
		PiGraph graph = (PiGraph) bo;
		
		/* Create Document Builder */
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
        Document doc = builder.newDocument();
        
        /* Populate XML Files with File Header */
        Element svg = doc.createElement("svg");
        doc.appendChild(svg);
        svg.setAttribute("font-family",	"Arial");
        svg.setAttribute("xmlns", 	"http://www.w3.org/2000/svg");
        
        Element defs = doc.createElement("defs");
        svg.appendChild(defs);
        
        Element fifoMarker = doc.createElement("marker");
        defs.appendChild(fifoMarker);{
	        fifoMarker.setAttribute("id", "fifoEnd");
	        fifoMarker.setAttribute("markerWidth", "4");
	        fifoMarker.setAttribute("markerHeight", "4");
	        fifoMarker.setAttribute("refX", "4");
	        fifoMarker.setAttribute("refY", "2");
	        Element polygon = doc.createElement("polygon");
	        fifoMarker.appendChild(polygon);
	        polygon.setAttribute("points", "0,0 5,2 0,4");
	        polygon.setAttribute("fill", "rgb(100, 100, 100)");
	        polygon.setAttribute("stroke-width", "none");
        }
        
        Element depMarker = doc.createElement("marker");
        defs.appendChild(depMarker);{
        	depMarker.setAttribute("id", "depEnd");
        	depMarker.setAttribute("markerWidth", "4");
        	depMarker.setAttribute("markerHeight", "4");
        	depMarker.setAttribute("refX", "4");
        	depMarker.setAttribute("refY", "2");
	        Element polygon = doc.createElement("polygon");
	        depMarker.appendChild(polygon);
	        polygon.setAttribute("points", "0,0 5,2 0,4");
	        polygon.setAttribute("fill", "rgb(98, 131, 167)");
	        polygon.setAttribute("stroke-width", "none");
        }
        
        /* Populate SVG File with Graph Data */ 
        SVGExporterSwitch visitor = new SVGExporterSwitch(doc, svg);
        for(Dependency d : graph.getDependencies()){
        	visitor.doSwitch(d);
		}
        for(Fifo f : graph.getFifos()){
        	visitor.doSwitch(f);
		}
        for(Parameter p : graph.getParameters()){
        	visitor.doSwitch(p);
		}
        for(AbstractActor aa : graph.getVertices()){
        	visitor.doSwitch(aa);
		}

        svg.setAttribute("width", 	""+(visitor.getTotalWidth()+20));
        svg.setAttribute("height",  ""+(visitor.getTotalHeight()+20));
        
        /* Write the SVG File */
        Transformer tf;
		try {
			tf = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
			return;
		}
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        /* Ask SVG File Location */
        Set<String> fileExtensions = new HashSet<String>();
        fileExtensions.add("*.svg");
        IPath path = PiMMUtil.askSaveFile("Choose the exported SVG file", fileExtensions);
        
        if(path == null) return;
     		
        File svgFile = new File(path.toOSString());
        Writer out;
		try {
			out = new FileWriter(svgFile);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
        try {
			tf.transform(new DOMSource(doc), new StreamResult(out));
		} catch (TransformerException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public class SVGExporterSwitch extends PiMMSwitch<Integer>{
		protected Document doc;
		protected Element svg;
		protected int totalWidth;
		protected int totalHeight;
		
		/**
		 * @return the totalWidth
		 */
		public int getTotalWidth() {
			return totalWidth;
		}

		/**
		 * @return the totalHeight
		 */
		public int getTotalHeight() {
			return totalHeight;
		}

		public SVGExporterSwitch(Document doc, Element svg){
			this.doc = doc;
			this.svg = svg;
			totalWidth = 0;
			totalHeight = 0;
		}
				
		public Integer caseParameter(Parameter p) {
			if(p.isLocallyStatic() && p.getGraphPort() instanceof ConfigInputPort)
				return caseConfigInputInterface(p);
			
			int x = 0, y = 0, width = 0, height = 0;
			PictogramElement[] paramPes = fp.getAllPictogramElementsForBusinessObject(p);
			if (paramPes == null) return null;
			
			x = paramPes[0].getGraphicsAlgorithm().getX();
			y = paramPes[0].getGraphicsAlgorithm().getY();
			width = paramPes[0].getGraphicsAlgorithm().getWidth();
			height = paramPes[0].getGraphicsAlgorithm().getHeight();
			
			totalWidth = java.lang.Math.max(x+width,totalWidth);
			totalHeight = java.lang.Math.max(y+height,totalHeight);
			
	        Element paramNode = doc.createElement("g");
	        svg.appendChild(paramNode);
	        paramNode.setAttribute("id", p.getName());
	        paramNode.setAttribute("transform", "translate(" + x + "," + y + ")");	        
	        {
		        Element polygon = doc.createElement("polygon"); 
		        paramNode.appendChild(polygon);
		        polygon.setAttribute("points", 
	        				"0," + (height) + " " +
	        				"0," + (height/2) + " " +
    						(width/2) + ",0 " + 
		        			(width) + "," + (height/2) +" " + 
		        			(width) + "," + (height));
		        polygon.setAttribute("fill", "rgb(187,218,247)");
		        polygon.setAttribute("stroke", "rgb(98,131,167)");
		        polygon.setAttribute("stroke-width", "4px");
		        
		        if(!p.isLocallyStatic()){
			        Element circle = doc.createElement("circle");
			        paramNode.appendChild(circle);
			        circle.setAttribute("cx", ""+(width/2));
			        circle.setAttribute("cy", "15");
			        circle.setAttribute("r",  "6");
			        circle.setAttribute("fill", "white");
			        circle.setAttribute("stroke-width", "2px");
			        circle.setAttribute("stroke", "rgb(98,131,167)");
		        }
		        
		        Element text = doc.createElement("text");
		        paramNode.appendChild(text);
		        text.setAttribute("x", ""+(width/2));
		        text.setAttribute("y", ""+(height-5));
		        text.setAttribute("fill", "black");
		        text.setAttribute("text-anchor", "middle");
		        addFontToSVG(text, getFont(p));
		        text.appendChild(doc.createTextNode(p.getName()));
	        }			
			return 0;
		}
		
		public Integer caseDataInputInterface(DataInputInterface dii) {
			int x = 0, y = 0;
			int width=0, height=0;
			PictogramElement[] diiPes = fp.getAllPictogramElementsForBusinessObject(dii);
			if (diiPes != null) {
				x = diiPes[1].getGraphicsAlgorithm().getX();
				y = diiPes[1].getGraphicsAlgorithm().getY();
				width = diiPes[1].getGraphicsAlgorithm().getWidth();
				height = diiPes[1].getGraphicsAlgorithm().getHeight();
			}

			totalWidth = java.lang.Math.max(x+width,totalWidth);
			totalHeight = java.lang.Math.max(y+height,totalHeight);
					
		    Element diiNode = doc.createElement("g");
		    svg.appendChild(diiNode);
		    diiNode.setAttribute("id", dii.getName());
		    diiNode.setAttribute("transform", "translate(" + x + "," + y + ")");	        
		    {
		        Element rect = doc.createElement("rect"); 
		        diiNode.appendChild(rect);
		        rect.setAttribute("rx", "2");
		        rect.setAttribute("ry", "2");
		        rect.setAttribute("x", ""+(width-16));
		        rect.setAttribute("y", "0");
		        rect.setAttribute("width", "16");
		        rect.setAttribute("height","16");
		        rect.setAttribute("fill","rgb(182, 215, 122)");
		        rect.setAttribute("stroke", "rgb(100,100,100)");
		        rect.setAttribute("stroke-width", "3px");
		        		        
		        Element text = doc.createElement("text");
		        diiNode.appendChild(text);
		        text.setAttribute("x", "2");
		        text.setAttribute("y", "11");
		        text.setAttribute("fill", "black");
		        text.setAttribute("text-anchor", "start");
		        addFontToSVG(text, getFont(dii));
		        text.appendChild(doc.createTextNode(dii.getName()));
		    }			
			return 0;
		}
		
		public Integer caseDataOutputInterface(DataOutputInterface doi) {
			int x = 0, y = 0;
			int width=0, height=0;
			PictogramElement[] doiPes = fp.getAllPictogramElementsForBusinessObject(doi);
			if (doiPes != null) {
				x = doiPes[1].getGraphicsAlgorithm().getX();
				y = doiPes[1].getGraphicsAlgorithm().getY();
				width = doiPes[1].getGraphicsAlgorithm().getWidth();
				height = doiPes[1].getGraphicsAlgorithm().getHeight();
			}

			totalWidth = java.lang.Math.max(x+width,totalWidth); 
			totalHeight = java.lang.Math.max(y+height,totalHeight);
					
		    Element doiNode = doc.createElement("g");
		    svg.appendChild(doiNode);
		    doiNode.setAttribute("id", doi.getName());
		    doiNode.setAttribute("transform", "translate(" + x + "," + y + ")");	        
		    {
		        Element rect = doc.createElement("rect"); 
		        doiNode.appendChild(rect);
		        rect.setAttribute("rx", "2");
		        rect.setAttribute("ry", "2");
		        rect.setAttribute("x", "0");
		        rect.setAttribute("y", "0");
		        rect.setAttribute("width", "16");
		        rect.setAttribute("height","16");
		        rect.setAttribute("fill","rgb(234, 153, 153)");
		        rect.setAttribute("stroke", "rgb(100,100,100)");
		        rect.setAttribute("stroke-width", "3px");
		        		        
		        Element text = doc.createElement("text");
		        doiNode.appendChild(text);
		        text.setAttribute("x", "21");
		        text.setAttribute("y", "11");
		        text.setAttribute("fill", "black");
		        text.setAttribute("text-anchor", "start");
		        addFontToSVG(text, getFont(doi));
		        text.appendChild(doc.createTextNode(doi.getName()));
		    }			
			return 0;
		}
	
		public Integer caseConfigInputInterface(Parameter cii) {
			int x = 0, y = 0;
			int width=0, height=0;
			PictogramElement[] ciiPes = fp.getAllPictogramElementsForBusinessObject(cii);
			if (ciiPes != null) {
				x = ciiPes[2].getGraphicsAlgorithm().getX();
				y = ciiPes[2].getGraphicsAlgorithm().getY();
				width = ciiPes[2].getGraphicsAlgorithm().getWidth();
				height = ciiPes[2].getGraphicsAlgorithm().getHeight();
			}

			totalWidth = java.lang.Math.max(x+width,totalWidth); 
			totalHeight = java.lang.Math.max(y+height,totalHeight);
					
		    Element ciiNode = doc.createElement("g");
		    svg.appendChild(ciiNode);
		    ciiNode.setAttribute("id", cii.getName());
		    ciiNode.setAttribute("transform", "translate(" + x + "," + y + ")");	        
		    {
		        Element polygon = doc.createElement("polygon"); 
		        Polygon polyPe = (Polygon)ciiPes[0].getGraphicsAlgorithm();
		        ciiNode.appendChild(polygon);
		        String points = "";
		        for(Point p : polyPe.getPoints()){
		        	points += (p.getX()+3)+","+(p.getY()+16)+" "; 
		        }
		        polygon.setAttribute("points", points);
		        polygon.setAttribute("fill", "rgb(187, 218, 247)");
		        polygon.setAttribute("stroke", "rgb(98,131,167)");
		        polygon.setAttribute("stroke-width", "3px");
		        		        
		        Element text = doc.createElement("text");
		        ciiNode.appendChild(text);
		        text.setAttribute("x", ""+width/2);
		        text.setAttribute("y", "10");
		        text.setAttribute("fill", "black");
		        text.setAttribute("text-anchor", "middle");
		        addFontToSVG(text, getFont(cii));
		        text.appendChild(doc.createTextNode(cii.getName()));
		    }			
			return 0;
		}
	
		public Integer caseConfigOutputInterface(ConfigOutputInterface coi) {
			int x = 0, y = 0;
			PictogramElement[] coiPes = fp.getAllPictogramElementsForBusinessObject(coi);
			if (coiPes != null) {
				x = coiPes[1].getGraphicsAlgorithm().getX();
				y = coiPes[1].getGraphicsAlgorithm().getY();
			}

			totalWidth = java.lang.Math.max(x+16,totalWidth); // TODO Adjust size
			totalHeight = java.lang.Math.max(y+16,totalHeight);
					
		    Element coiNode = doc.createElement("g");
		    svg.appendChild(coiNode);
		    coiNode.setAttribute("id", coi.getName());
		    coiNode.setAttribute("transform", "translate(" + x + "," + y + ")");	        
		    {
		        Element polygon = doc.createElement("polygon"); 
		        coiNode.appendChild(polygon);
		        polygon.setAttribute("points", "0,0 16,8 0,16");
		        polygon.setAttribute("fill","rgb(255, 229, 153)");
		        polygon.setAttribute("stroke", "rgb(100,100,100)");
		        polygon.setAttribute("stroke-width", "3px");
		        		        
		        Element text = doc.createElement("text");
		        coiNode.appendChild(text);
		        text.setAttribute("x", "21");
		        text.setAttribute("y", "11");
		        text.setAttribute("fill", "black");
		        text.setAttribute("text-anchor", "start");
		        addFontToSVG(text, getFont(coi));
			    text.appendChild(doc.createTextNode(coi.getName()));
		    }			
			return 0;
		}
		
		public Integer caseExecutableActor(ExecutableActor ea) {
			int x = 0, y = 0, width = 0, height = 0;
			PictogramElement[] actorPes = fp.getAllPictogramElementsForBusinessObject(ea);
			if (actorPes == null) return null;
				
			x = actorPes[0].getGraphicsAlgorithm().getX();
			y = actorPes[0].getGraphicsAlgorithm().getY();
			width = actorPes[0].getGraphicsAlgorithm().getWidth();
			height = actorPes[0].getGraphicsAlgorithm().getHeight();
						
			totalWidth = java.lang.Math.max(x+width,totalWidth);
			totalHeight = java.lang.Math.max(y+height,totalHeight);
			
			/* Draw Actor */
	        Element actorNode = doc.createElement("g");
	        svg.appendChild(actorNode);
	        actorNode.setAttribute("id", ea.getName());
	        actorNode.setAttribute("transform", "translate(" + x + "," + y + ")");	        
	        {
	    		ContainerShape containerShape = (ContainerShape) actorPes[0];
	    		EList<Shape> childrenShapes = containerShape.getChildren();

	    		Text nameText = null;
	    		RoundedRectangle actorRect = (RoundedRectangle) actorPes[0].getGraphicsAlgorithm();
	    		for (Shape shape : childrenShapes) {
	    			GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
	    			if (child instanceof Text) {
	    				nameText = (Text)child;
	    			}
	    		}
    		
		        Element rect = doc.createElement("rect"); 
		        actorNode.appendChild(rect);
		        rect.setAttribute("rx", "4");
		        rect.setAttribute("ry", "4");
		        rect.setAttribute("width",  ""+width);
		        rect.setAttribute("height", ""+height);
		        
		        rect.setAttribute("fill", "rgb(" +
		        		actorRect.getBackground().getRed() + ", " +
		        		actorRect.getBackground().getGreen() + ", " +
		        		actorRect.getBackground().getBlue() + ")");

		        rect.setAttribute("stroke", "rgb(" +
		        		actorRect.getForeground().getRed() + ", " +
		        		actorRect.getForeground().getGreen() + ", " +
		        		actorRect.getForeground().getBlue() + ")");
		        
		        rect.setAttribute("stroke-width", "3px");
		        
		        if(!ea.getConfigOutputPorts().isEmpty()){
			        Element circle = doc.createElement("circle");
			        actorNode.appendChild(circle);
			        circle.setAttribute("cx", ""+(width-8));
			        circle.setAttribute("cy", "9");
			        circle.setAttribute("r",  "4");
			        circle.setAttribute("fill", "white");
			        circle.setAttribute("stroke-width", "2px");
			        circle.setAttribute("stroke", "rgb(100,100,100)");
		        }
		        
		        Element text = doc.createElement("text");
		        actorNode.appendChild(text);
		        configTextToSVG(text, nameText);
		        text.appendChild(doc.createTextNode(ea.getName()));
	        }			
	        
	        /* Draw Config Input Ports */
	        for(int i=0; i<ea.getConfigInputPorts().size(); i++){
	        	ConfigInputPort cip = ea.getConfigInputPorts().get(i);
	        	BoxRelativeAnchor bra = getPortBra(cip);
	        	
				int portX = (int) (bra.getRelativeWidth()*width);
				int portY = (int) (bra.getRelativeHeight()*height);
				Text portText = null;
						
				for (GraphicsAlgorithm ga : bra.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) 
					if (ga instanceof Text) 
						portText = (Text)ga;
							
				if(portText == null) return null;
				
	        	Element portNode = doc.createElement("g");
		        actorNode.appendChild(portNode);
		        portNode.setAttribute("id", cip.getName());
		        portNode.setAttribute("transform", "translate(" + portX + "," + portY + ")");	        
		        {
			        Element polygon = doc.createElement("polygon"); 
			        portNode.appendChild(polygon);
			        polygon.setAttribute("points", "0,0 8,5 0,10");
			        polygon.setAttribute("fill", "rgb(187, 218, 247)");
			        polygon.setAttribute("stroke", "rgb(100,100,100)");
			        polygon.setAttribute("stroke-width", "1px");
			        
			       	Element text = doc.createElement("text");
			        portNode.appendChild(text);
			        configTextToSVG(text, portText);
			        text.appendChild(doc.createTextNode(cip.getName()));
		        }			
	        }	
	        
	        /* Draw Config Output Ports */
	        for(int i=0; i<ea.getConfigOutputPorts().size(); i++){
	        	ConfigOutputPort cop = ea.getConfigOutputPorts().get(i);
	        	BoxRelativeAnchor bra = getPortBra(cop);
	        	
				int portX = (int) (bra.getRelativeWidth()*width);
				int portY = (int) (bra.getRelativeHeight()*height);
				Text portText = null;
						
				for (GraphicsAlgorithm ga : bra.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) 
					if (ga instanceof Text) 
						portText = (Text)ga;
							
				if(portText == null) return null;
				
	        	Element portNode = doc.createElement("g");
		        actorNode.appendChild(portNode);
		        portNode.setAttribute("id", cop.getName());
		        portNode.setAttribute("transform", "translate(" + portX + "," + portY + ")");	        
		        {
			        Element polygon = doc.createElement("polygon"); 
			        portNode.appendChild(polygon);
			        polygon.setAttribute("points", "0,0 -8,5 0,10");
			        polygon.setAttribute("fill", "rgb(255, 229, 153)");
			        polygon.setAttribute("stroke", "rgb(100,100,100)");
			        polygon.setAttribute("stroke-width", "1px");

			        Element text = doc.createElement("text");
			        portNode.appendChild(text);
			        configTextToSVG(text, portText);
			        text.appendChild(doc.createTextNode(cop.getName()));
		        }			
	        }

	        /* Draw Data Input Ports */
	        for(int i=0; i<ea.getDataInputPorts().size(); i++){
	        	DataInputPort dip = ea.getDataInputPorts().get(i);
	        	BoxRelativeAnchor bra = getPortBra(dip);

				int portX = (int) (bra.getRelativeWidth()*width);
				int portY = (int) (bra.getRelativeHeight()*height);
				Text portText = null;
						
				for (GraphicsAlgorithm ga : bra.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) 
					if (ga instanceof Text) 
						portText = (Text)ga;
							
				if(portText == null) return null;
	        	
	        	Element portNode = doc.createElement("g");
		        actorNode.appendChild(portNode);
		        portNode.setAttribute("id", dip.getName());
		        portNode.setAttribute("transform", "translate(" + portX + "," + portY + ")");	        
		        {
			        Element rect = doc.createElement("rect"); 
			        portNode.appendChild(rect);
			        rect.setAttribute("x", "0");
			        rect.setAttribute("y", "1");
			        rect.setAttribute("width", "8");
			        rect.setAttribute("height","8");
			        rect.setAttribute("fill","rgb(182, 215, 122)");
			        rect.setAttribute("stroke", "rgb(100,100,100)");
			        rect.setAttribute("stroke-width", "1px");
			        
			        Element text = doc.createElement("text");
			        portNode.appendChild(text);
			        configTextToSVG(text, portText);
			        text.appendChild(doc.createTextNode(dip.getName()));
		        }			
	        }

	        /* Draw Data Output Ports */
	        for(int i=0; i<ea.getDataOutputPorts().size(); i++){
	        	DataOutputPort dop = ea.getDataOutputPorts().get(i);
	        	BoxRelativeAnchor bra = getPortBra(dop);

				int portX = (int) (bra.getRelativeWidth()*width);
				int portY = (int) (bra.getRelativeHeight()*height);
				Text portText = null;
						
				for (GraphicsAlgorithm ga : bra.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) 
					if (ga instanceof Text) 
						portText = (Text)ga;
							
				if(portText == null) return null;
	        	
	        	Element portNode = doc.createElement("g");
		        actorNode.appendChild(portNode);
		        portNode.setAttribute("id", dop.getName());
		        portNode.setAttribute("transform", "translate(" + portX + "," + portY + ")");	        
		        {
			        Element rect = doc.createElement("rect"); 
			        portNode.appendChild(rect);
			        rect.setAttribute("x", "0");
			        rect.setAttribute("y", "1");
			        rect.setAttribute("width", "-8");
			        rect.setAttribute("height","8");
			        rect.setAttribute("fill","rgb(234, 153, 153)");
			        rect.setAttribute("stroke", "rgb(100,100,100)");
			        rect.setAttribute("stroke-width", "1px");
			        
			        Element text = doc.createElement("text");
			        portNode.appendChild(text);
			        configTextToSVG(text, portText);
			        text.appendChild(doc.createTextNode(dop.getName()));
		        }			
	        }
	        
			return 1;
		}
		
		public Integer caseDependency(Dependency d){
			FreeFormConnection ffc = getDepFFC(d);
			
			Element depNode = doc.createElement("path");
	        svg.appendChild(depNode);     
	        
	        ILocation start = Graphiti.getPeLayoutService()
					.getLocationRelativeToDiagram((Anchor) ffc.getStart());
	        
	        if(d.getSetter() instanceof ConfigOutputPort)
	        	start.setY(start.getY() + 5);
	        
	        ILocation end   = Graphiti.getPeLayoutService()
					.getLocationRelativeToDiagram((Anchor) ffc.getEnd());
	        
	        
	        if(d.getGetter().eContainer() instanceof Parameter){
	        	Parameter p = (Parameter) d.getGetter().eContainer();
	        	PictogramElement[] pPes = fp.getAllPictogramElementsForBusinessObject(p);
	        	end.setX(end.getX() - pPes[0].getGraphicsAlgorithm().getWidth()/2);	        	
	        }else{
	        	end.setY(end.getY() + 5);
	        }

	        String points = "m ";
	        int prevX = start.getX();
	        int prevY = start.getY();
	        points = points + start.getX() + "," + start.getY() + " ";
	        for(org.eclipse.graphiti.mm.algorithms.styles.Point p : ffc.getBendpoints()){
	        	points = points + (p.getX()-prevX) + "," + (p.getY()-prevY) + " ";
	        	prevX = p.getX();
	        	prevY = p.getY();
	        }
	        points = points + (end.getX()-prevX) + "," + (end.getY()-prevY) + " ";
	        
	        depNode.setAttribute("d", points);
	        depNode.setAttribute("fill", "none");
	        depNode.setAttribute("stroke", "rgb(98, 131, 167)");
	        depNode.setAttribute("stroke-width", "3px");
	        depNode.setAttribute("stroke-dasharray", "5,2");
	        depNode.setAttribute("marker-end", "url(#depEnd)");		        
		      			
			return 0;
		}
		
		public Integer caseFifo(Fifo f){
			Set<FreeFormConnection> ffcs = getFifoFFC(f);
			
			for(FreeFormConnection ffc : ffcs){
				Element depNode = doc.createElement("path");
		        svg.appendChild(depNode);     
		        
		        ILocation start = Graphiti.getPeLayoutService()
						.getLocationRelativeToDiagram((Anchor) ffc.getStart());
		        
		        if(f.getSourcePort() instanceof DataOutputPort)
		        	start.setY(start.getY() + 5);
		        
		        ILocation end   = Graphiti.getPeLayoutService()
						.getLocationRelativeToDiagram((Anchor) ffc.getEnd());
		        
		        if(f.getTargetPort() instanceof DataInputPort)
		        	end.setY(end.getY() + 5);
		        
		        String points = "m ";
		        int prevX = start.getX();
		        int prevY = start.getY();
		        points = points + start.getX() + "," + start.getY() + " ";
		        for(org.eclipse.graphiti.mm.algorithms.styles.Point p : ffc.getBendpoints()){
		        	points = points + (p.getX()-prevX) + "," + (p.getY()-prevY) + " ";
		        	prevX = p.getX();
		        	prevY = p.getY();
		        }
		        points = points + (end.getX()-prevX) + "," + (end.getY()-prevY) + " ";

		        depNode.setAttribute("d", points);
		        depNode.setAttribute("fill", "none");
		        depNode.setAttribute("stroke", "rgb(100, 100, 100)");
		        depNode.setAttribute("stroke-width", "3px");
		        depNode.setAttribute("marker-end", "url(#fifoEnd)");		  
			}
			
			if(f.getDelay() != null){
				PictogramElement[] pes = fp.getAllPictogramElementsForBusinessObject(f.getDelay());
				Ellipse delay = (Ellipse)(pes[0].getGraphicsAlgorithm());
				
				Element circle = doc.createElement("circle"); 
		        svg.appendChild(circle);
		        circle.setAttribute("cx", ""+(delay.getX()+12));
		        circle.setAttribute("cy", ""+(delay.getY()+12));
		        circle.setAttribute("r", "8");
		        circle.setAttribute("fill","rgb(100,100,100)");
		        circle.setAttribute("stroke", "rgb(100,100,100)");
		        circle.setAttribute("stroke-width", "1px");
		        
				pes[0].getLink();
			}
		      			
			return 0;
		}
	}
}
