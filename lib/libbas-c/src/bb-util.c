#include <string.h>

#include <bas/bb-util.h>

#define LOG_IDENT "bb-util"
#define LOG_LEVEL 3
#include <bas/log.h>

#define log log_info

void bb_dump_var(SHELL_VAR *var) {
    log("Variable %s: ", var->name);

    char buf[1000] = "";
    int attrs = var->attributes;
    if (attrs & att_exported)   strcat(buf, " exported");
    if (attrs & att_readonly)   strcat(buf, " readonly");
    if (attrs & att_array)      strcat(buf, " array");
    if (attrs & att_function)   strcat(buf, " function");
    if (attrs & att_integer)    strcat(buf, " integer");
    if (attrs & att_local)      strcat(buf, " local");
    if (attrs & att_assoc)      strcat(buf, " assoc");
    if (attrs & att_trace)      strcat(buf, " trace");
    if (attrs & att_uppercase)  strcat(buf, " uppercase");
    if (attrs & att_lowercase)  strcat(buf, " lowercase");
    if (attrs & att_capcase)    strcat(buf, " capcase");
    if (attrs & att_invisible)  strcat(buf, " invisible");
    if (attrs & att_nounset)    strcat(buf, " nounset");
    if (attrs & att_noassign)   strcat(buf, " noassign");
    if (attrs & att_imported)   strcat(buf, " imported");
    if (attrs & att_special)    strcat(buf, " special");
    if (attrs & att_nofree)     strcat(buf, " nofree");
    if (attrs & att_tempvar)    strcat(buf, " tempvar");
    if (attrs & att_propagate)  strcat(buf, " propagate");
    log("    attributes: %s", buf);

    log("    value: (%x) %s", var->value, var->value);
    if (var->exportstr)
        log("    exportstr: %s", var->exportstr);
    if (var->context)
        log("    context: %d", var->context);
    if (var->dynamic_value)
        log("    dynamic_value: %x", var->dynamic_value);
    if (var->assign_func)
        log("    assign_func: %x", var->assign_func);
}

void bb_dump_array(ARRAY *array) {
    log("%s array of %d elements, max_index=%d:",
        array->type == array_indexed ? "Indexed" :
        array->type == array_assoc ? "Assoc" : "Unknown",
        array->num_elements, array->max_index);

    int i;

    ARRAY_ELEMENT *elm = array->head->next;
    for (i = 0; i < array->num_elements; i++) {
        char *val = elm->value;
        int ind = elm->ind;
        log("    Element [%d] = %s.", ind, val);
        elm = elm->next;
    }
}

void bb_dump_words(WORD_LIST *list) {
    if (list == NULL)
        log("Null word list.");
    else
        log("Word list %x:", list);
    
    while (list) {
        log("    word: %s.", list->word->word);
        list = list->next;
    }
}
