
import json

class ErrorResponse():
    def __init__(self, error_code, description):
        self.error_code = error_code
        self.description = description
    
    def get_json_dict(self):
        return {"error": self.error_code, "description": self.description}

LOGOUT_OK = ErrorResponse(2001, "logout_ok").get_json_dict()
CLAN_CHANGED = ErrorResponse(2002, "clan_changed").get_json_dict()
CAPTURE_STARTED = ErrorResponse(2003, "capture_started").get_json_dict()
BAD_REQUEST = ErrorResponse(4001, "bad_request").get_json_dict()
BAD_PASSWORD = ErrorResponse(4002, "bad_password").get_json_dict()
BAD_COOKIE = ErrorResponse(4003, "bad_cookie").get_json_dict()
NOT_FOUND = ErrorResponse(4004, "not_found").get_json_dict()
ALREADY_EXISTS = ErrorResponse(4005, "already_exists").get_json_dict()
SERVER_ERROR = ErrorResponse(5001, "server_error").get_json_dict()