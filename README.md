# Anti-fraud Science Communication Platform

This project is a full-stack application with a Vue.js frontend and Spring Boot backend for managing user authentication and communication.

## Project Structure

```
.
├── front-end/          # Vue.js frontend application
└── server/            # Spring Boot backend application
```

## Prerequisites

Before you begin, ensure you have installed:

- Node.js (v16 or higher)
- pnpm (v8 or higher)
- Java JDK (v17 or higher)
- Maven (v3.6 or higher)

### Installing Prerequisites

1. Install Node.js:
   ```bash
   # For macOS using Homebrew
   brew install node
   ```

2. Install pnpm:
   ```bash
   npm install -g pnpm
   ```

3. Install Java JDK:
   ```bash
   # For macOS using Homebrew
   brew install openjdk@17
   ```

4. Install Maven:
   ```bash
   # For macOS using Homebrew
   brew install maven
   ```

## Frontend Setup (Vue.js)

1. Navigate to the frontend directory:
   ```bash
   cd front-end
   ```

2. Install dependencies:
   ```bash
   pnpm install
   ```

3. Start development server:
   ```bash
   pnpm dev
   ```

4. Build for production:
   ```bash
   pnpm build
   ```

The frontend will be available at `http://localhost:5173` by default.

## Backend Setup (Spring Boot)

1. Navigate to the backend directory:
   ```bash
   cd server
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The backend API will be available at `http://localhost:8080`.

## Features

### User Management
- User registration and login
- User profile management
- Password reset functionality
- User role management (Admin/User)
- User list with pagination and search

### Security Features
- Password encryption
- Session management
- Role-based access control

## Default Accounts

The system automatically creates an admin account on first run:
- Username: admin
- Password: 123456

## API Documentation

### User Endpoints

- POST `/user/register` - Register new user
- POST `/user/login` - User login
- GET `/user/info` - Get user information
- POST `/user/logout` - User logout
- GET `/user/list` - Get user list (Admin only)
- POST `/user/add` - Add new user (Admin only)
- PUT `/user/update` - Update user information
- DELETE `/user/delete` - Delete user (Admin only)
- POST `/user/reset-password` - Reset user password (Admin only)
- POST `/user/change-password` - Change user password

## Development Notes

1. The frontend uses:
   - Vue 3 with Composition API
   - Ant Design Vue for UI components
   - Axios for API requests
   - Dayjs for date handling

2. The backend uses:
   - Spring Boot
   - Spring Security
   - Spring Data JPA
   - Password encryption with BCrypt

## Production Deployment

1. Build the frontend:
   ```bash
   cd front-end
   pnpm build
   ```

2. Build the backend:
   ```bash
   cd server
   mvn clean package
   ```

3. The backend jar file will be generated in `server/target/`.

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## License

This project is licensed under the MIT License.
