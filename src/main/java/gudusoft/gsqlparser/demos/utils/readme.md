## Description

Shared helpers, not a demo — there is no `main()` here.

| Class | Purpose |
|-------|---------|
| `TJsonSQLEnv` | A `TSQLEnv` backed by a JSON file, so the parser can resolve schema-dependent names without a database connection. Iterable over the queries it holds. |
| `SQLQuery` | One query plus its metadata |
| `SQLUtil` | File and string helpers used by the above |

`TJsonSQLEnv` is what the `sqlenv` demo runs on; see that folder for how to
invoke it and what the JSON is for.
