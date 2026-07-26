package demos.antiSQLInjection;


public enum ESQLInjectionType {
    syntax_error,always_true_condition,always_false_condition,comment_at_the_end_of_statement,
    stacking_queries,not_in_allowed_statement,union_set,piggybacked_statement
}
