package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,

    /* user interface icon file names */
    SCREENSHOT_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    //TEXT_AREA,
    //SPECIFIED_FILE,
    DISPLAY,
    DATA_VISAULISATION,
    //DATA_FILE,
    EXIT_WHILE_RUNNING_WARNING,
    UNVALID_DATA,
    //TOO_MANY_LINES_TITLE,
    //TOO_MANY_LINES_MSG,
    //LINES,
    DUPLICATE_ERROR_TITLE,
    DUPLICATE_ERROR_MSG,
    LINE_NO,
    //CHART_IS_EMPTY_TITLE,
    //CHART_IS_EMPTY_MSG,
    READ_ONLY,
    IMAGE_EXT,
    IMAGE_EXT_DESC,
    PROPERTIES_CSS_DIR,
    CHART_LINE_SYMBOL,
    FX_BACKGROUND_TRANSPARENT,
    FX_FONT_SIZE,
    ALGORITHM_TYPE,
    CLASSIFICATION,
    CLUSTERING,
    NOTHING,
    INSTANCES_WITH,
    LABELS_LOADED_FROM,
    LABELS_ARE,
    ALGORITHM_A,
    ALGORITHM_B,
    ALGORITHM_C,
    SETTINGS,
    DONE,
    MAXIMUM_IT,
    UPDATE_INT,
    CONT_RUN,
    CLUSTERS,
    AND_THE_PATH,
    RAND_CLUST,
    KMEANS,
    CLUS,
    CLUST,
    ALGS


}
