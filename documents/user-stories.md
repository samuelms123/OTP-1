
## User Stories

### User Authentication & Account Management

---

**User Story 1: As a new user, I want to create an account so that I can access the private social platform.**

Acceptance Criteria:

*   A "Register" button is available on the login screen.

*   The registration form requires a unique username, a valid email, and a password.

*   The app validates that the username and email are not already in use.

*   Upon successful registration, the user's credentials are saved securely in the database, and they are automatically logged in and taken to the main feed.

---

**User Story 2: As a registered user, I want to log into my account securely so that I can access my private content.**

Acceptance Criteria:

*    The login screen has fields for username/email and password.

*    Failed login attempts provide a generic error message (e.g., "Invalid credentials").

*    Successful login leads the user to the application's main dashboard/feed.

---

**User Story 3: As a logged-in user, I want to log out so that I can keep my account secure, especially on a shared PC.**

Acceptance Criteria:

*    A clearly visible "Log Out" button is present in the application.

*    Clicking it terminates the user's session and returns them to the login screen.

---

### Managing Friends & Connections

---

**User Story 4: As a user, I want to see my friends list so that I can keep track with whom I'm posting with.**

Acceptance Criteria:

*   List user friends under profile view.


---

**User Story 5: As a user, I want to send friend requests to other users so that I can connect with them on the platform.**

Acceptance Criteria:

*   An 'Add Friend!' functionality is available to find other users by username or email.
*   The recipient should be able to accept or decline.

---

### Creating and Viewing Content

---

**User Story 6: As a user, I want to create posts so that I can communicate with my friends.**

Acceptance Criteria:
*   "Add New Post" button is available on the main feed.
*   The post creation form allows text input and optional image upload.
*   Upon submission, the post appears in the main feed and is visible to all friends.

---

### Interacting with Content

---

**User Story 7: As a user, I want to like and comment on my friends' posts so that I can engage with their content.**
Acceptance Criteria:
*   Each post has "Like" and "Comment" buttons.

---

### User Profile & Customization

---

**User Story 8: As a user, I want to view and edit my profile so that I can manage my personal information and preferences.**
Acceptance Criteria:
*  A "Profile" section is accessible from the main feed.
*  Users can update their info.
*  Changes are saved and reflected immediately.

