package org.preesm.model.scenario.serialize;

/**
 * Class to centralise tag names used in scenario files
 *
 * @author hmiomandre
 */

public class ScenarioConstants {

  public static final String ACTOR_PATH        = "actorPath";
  public static final String ALGORITHM         = "algorithm";
  public static final String ARCHITECTURE      = "architecture";
  public static final String AVERAGE_DATA_SIZE = "averageDataSize";

  public static final String CODEGEN_DIRECTORY = "codegenDirectory";
  public static final String COMPONENT         = "component";
  public static final String COMPONENT_ID      = "componentId";
  public static final String COMPONENT_INDEX   = "componentIndex";
  public static final String COMPONENT_TYPE    = "componentType";
  public static final String CONSTRAINTS       = "constraints";
  public static final String CONSTRAINT_GROUP  = "constraintGroup";

  public static final String DATA_TYPE           = "dataType";
  public static final String DATA_TYPES          = "dataTypes";
  public static final String DESCRIPTION         = "description";
  public static final String DESTINATION_PE_TYPE = "destinationPeType";
  public static final String DESTINATION_TYPE    = "destinationType";

  public static final String ENERGY               = "energy";
  public static final String ENERGY_CONFIGS       = "energyConfigs";
  public static final String ENERGY_MODEL_PE_TYPE = "energyModelPEType";
  public static final String ENERGY_VALUE         = "energyValue";
  public static final String EVENT                = "event";
  public static final String EVENT_DESCRIPTION    = "eventDescription";
  public static final String EVENT_ID             = "eventId";
  public static final String EVENT_MODIFIER       = "eventModifier";
  public static final String EVENT_NAME           = "eventName";
  public static final String EVENT_SET            = "eventSet";
  public static final String EXCEL_URL            = "excelUrl";

  public static final String FILES = "files";
  public static final String FLAGS = "flags";

  public static final String MAIN_COM_NODE   = "mainComNode";
  public static final String MAIN_CORE       = "mainCore";
  public static final String MEMCPY_SPEED    = "memcpyspeed";
  public static final String MODEL_PARAMETER = "modelParameter";

  public static final String NAME = "name";

  public static final String OBJECTIVE_EPS = "objectiveEPS";
  public static final String OP_NAME       = "opName";
  public static final String OPERATOR      = "operator";
  public static final String OPNAME        = "opname";

  public static final String PAPI_COMPONENT       = "PAPIComponent";
  public static final String PAPI_EVENT           = "papiEvent";
  public static final String PAPIFY_CONFIGS       = "papifyConfigs";
  public static final String PAPIFY_CONFIG_ACTOR  = "papifyConfigActor";
  public static final String PAPIFY_CONFIG_PE     = "papifyConfigPE";
  public static final String PARAMETER            = "parameter";
  public static final String PARAM_VALUE          = "paramValue";
  public static final String PARAMETER_VALUES     = "parameterValues";
  public static final String PARENT               = "parent";
  public static final String PATH                 = "path";
  public static final String PE_ACTOR_ENERGY      = "peActorEnergy";
  public static final String PE_ACTORS_ENERGY     = "peActorsEnergy";
  public static final String PE_INSTANCE          = "peInstance";
  public static final String PE_POWER             = "pePower";
  public static final String PE_TYPE              = "peType";
  public static final String PE_TYPE_COMMS_ENERGY = "peTypeCommsEnergy";
  public static final String PERF_OBJECTIVE       = "performanceObjective";

  public static final String RELATIVE_CONSTRAINTS = "relativeconstraints";

  public static final String SETUP_TIME               = "setuptime";
  public static final String SIMU_PARAMS              = "simuParams";
  public static final String SIZE                     = "size";
  public static final String SIZE_ARE_IN_BIT          = "sizesAreInBit";
  public static final String SOURCE_PE_TYPE           = "sourcePeType";
  public static final String SPECIAL_VERTEX_OPERATOR  = "specialVertexOperator";
  public static final String SPECIAL_VERTEX_OPERATORS = "specialVertexOperators";

  public static final String TASK          = "task";
  public static final String TIME          = "time";
  public static final String TIME_PER_UNIT = "timeperunit";
  public static final String TIMING        = "timing";
  public static final String TIMING_TYPE   = "timingtype";
  public static final String TIMINGS       = "timings";
  public static final String TYPE          = "type";

  public static final String URL = "url";

  public static final String VALUE       = "value";
  public static final String VARIABLES   = "variables";
  public static final String VERTEX_NAME = "vertexname";

  public static final String XML_URL = "xmlUrl";

  private ScenarioConstants() {
    // Forbids instantiation
  }
}