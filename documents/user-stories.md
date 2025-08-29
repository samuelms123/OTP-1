
## User Stories

### User Authentication & Account Management

---

**User Story : As a new user, I want to create an account so that I can access the private social platform.**

Acceptance Criteria:

*   A "Register" button is available on the login screen.

*   The registration form requires a unique username, a valid email, and a password.

*   The app validates that the username and email are not already in use.

*   Upon successful registration, the user's credentials are saved securely in the database, and they are automatically logged in and taken to the main feed.

---

**User Story : As a registered user, I want to log into my account securely so that I can access my private content.**

Acceptance Criteria:

*    The login screen has fields for username/email and password.

*    Failed login attempts provide a generic error message (e.g., "Invalid credentials").

*    Successful login leads the user to the application's main dashboard/feed.

---

**User Story : As a logged-in user, I want to log out so that I can keep my account secure, especially on a shared PC.**

Acceptance Criteria:

*    A clearly visible "Log Out" button is present in the application.

*    Clicking it terminates the user's session and returns them to the login screen.

---

### Managing Friends & Connections

---

**User Story : As a user, I want to see my friends list so that I can keep track with whom I'm posting with.**

Acceptance Criteria:

*   List user friends under profile view.


---

**User Story : As a user, I want to send friend requests to other users so that I can connect with them on the platform.**

Acceptance Criteria:

*   An 'Add Friend!' functionality is available to find other users by username or email.
*   The recipient should be able to accept or decline.

---

**User Story : As a user, I want to remove users from my friends list.**

Acceptance Criteria:

*   'Remove friend' button in personal friends list and/or in users page.

---


### Creating, deleting and Viewing Content

---

**User Story : As a user, I want to create posts so that I can communicate with my friends.**

Acceptance Criteria:

*   "Add New Post" button is available on the main feed.
*   The post creation form allows text input and optional image upload.
*   Upon submission, the post appears in the main feed and is visible to all friends.

---

**User Story : As a user, I want to delete my own posts if necessary.**

Acceptance Criteria:
*   Delete button visible for the owner of the post.

---

**User Story : As a user, I want to delete my own comments from other users posts if necessary.**

Acceptance Criteria:
*   Delete button visible for the owner of the comment.

---

### Interacting with Content

---

**User Story : As a user, I want to like and comment on my friends' posts so that I can engage with their content.**

Acceptance Criteria:
*   Each post has "Like" and "Comment" buttons.

---

**User Story : As a user, I want to see who liked my post.**

Acceptance Criteria:
*   Make like amount on the post clickable and show list of usernames who liked the post.

---

### User Profile & Customization

---

**User Story : As a user, I want to view and edit my profile so that I can manage my personal information and preferences.**

Acceptance Criteria:
*  A "Profile" section is accessible from the main feed.
*  Users can update their info.
*  Changes are saved and reflected immediately.

---

**User Story : As a user, I want to change my password regularly so that I can keep my account secure.**

Acceptance Criteria:
*   Change password option in profile.

