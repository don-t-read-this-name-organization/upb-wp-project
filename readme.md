# University Website Management System

A comprehensive full-stack web application for managing university information including faculty profiles, timetables, and announcements. Built with modern technologies and designed with security and usability in mind.

## Features

### Professor Profiles
- Complete contact information (name, department, email, phone, office location, office hours)
- Biography and avatar support
- **No reviews or rating system** (as per requirements)
- Search and filter by department
- Card-based layout for modern UI

### Timetables (JSON-Based)
- Department-specific schedules
- JSON format storage for flexibility
- View by day or department
- Support for multiple semesters and academic years
- Dynamic rendering from JSON data

### Admin Panel
- Secure JWT-based authentication
- Full CRUD operations for:
  - Professors (Add, Edit, Delete)
  - Timetables (Add, Edit, Delete)
  - Announcements (Add, Edit, Delete)
- Responsive dashboard with statistics
- Protected routes

### Announcements
- Categories: General, Academic, Administrative, Events, Emergency
- Priority-based ordering
- Expiration dates support
- Published/Draft status

## Tech Stack

### Backend
- **Node.js** with Express.js
- **MongoDB** with Mongoose ODM
- **JWT** for authentication
- **bcryptjs** for password hashing
- **CORS** enabled for cross-origin requests

### Frontend
- **React.js** 18
- **React Router** for navigation
- **Axios** for API calls
- **CSS3** with CSS variables for theming
- Responsive design with mobile-first approach

## Project Structure

```
university-website-management/
├── backend/
│   ├── config/
│   │   └── db.js              # Database configuration
│   ├── controllers/
│   │   ├── authController.js
│   │   ├── professorController.js
│   │   ├── timetableController.js
│   │   └── announcementController.js
│   ├── data/
│   │   ├── sampleData.json    # Sample data for seeding
│   │   ├── seed.js            # Database seed script
│   │   └── timetable-example.json
│   ├── middleware/
│   │   └── auth.js            # JWT authentication middleware
│   ├── models/
│   │   ├── Professor.js
│   │   ├── Timetable.js
│   │   ├── Announcement.js
│   │   └── User.js
│   ├── routes/
│   │   ├── auth.js
│   │   ├── professors.js
│   │   ├── timetables.js
│   │   └── announcements.js
│   ├── package.json
│   ├── server.js              # Entry point
│   └── .env.example
├── frontend/
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── components/
│   │   │   ├── Navbar.js
│   │   │   ├── Footer.js
│   │   │   └── ProtectedRoute.js
│   │   ├── context/
│   │   │   └── AuthContext.js
│   │   ├── pages/
│   │   │   ├── Home.js
│   │   │   ├── Professors.js
│   │   │   ├── ProfessorDetail.js
│   │   │   ├── Timetable.js
│   │   │   ├── Announcements.js
│   │   │   ├── Login.js
│   │   │   ├── AdminDashboard.js
│   │   │   ├── NotFound.js
│   │   │   └── admin/
│   │   │       ├── ProfessorManager.js
│   │   │       ├── TimetableManager.js
│   │   │       └── AnnouncementManager.js
│   │   ├── App.js
│   │   ├── index.js
│   │   └── index.css
│   └── package.json
└── README.md
```

## Getting Started

### Prerequisites
- Node.js (v16 or higher)
- MongoDB (local or cloud instance)
- npm or yarn

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd university-website-management
```

2. **Setup Backend**
```bash
cd backend
npm install

# Create .env file from example
cp .env.example .env

# Edit .env with your configuration
# Especially update MONGODB_URI and JWT_SECRET
```

3. **Setup Frontend**
```bash
cd ../frontend
npm install
```

### Running the Application

**Option 1: Run both servers simultaneously**

```bash
# Terminal 1 - Backend
cd backend
npm run dev

# Terminal 2 - Frontend
cd frontend
npm start
```

**Option 2: Quick Start with Sample Data**

```bash
# Seed the database with sample data
cd backend
npm run dev

# In another terminal
cd backend/data
node seed.js
```

### Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:5000
- **API Documentation**: http://localhost:5000/api

### Default Login Credentials
After seeding the database:
- **Email**: admin@university.edu
- **Password**: admin123

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/me` - Get current user (protected)
- `PUT /api/auth/profile` - Update profile (protected)
- `PUT /api/auth/password` - Change password (protected)

### Professors
- `GET /api/professors` - Get all professors
- `GET /api/professors/:id` - Get single professor
- `GET /api/professors/departments/list` - Get unique departments
- `POST /api/professors` - Create professor (admin)
- `PUT /api/professors/:id` - Update professor (admin)
- `DELETE /api/professors/:id` - Delete professor (admin)

### Timetables
- `GET /api/timetables` - Get all timetables
- `GET /api/timetables/:id` - Get single timetable
- `GET /api/timetables/json/:department` - Get JSON format
- `GET /api/timetables/department/:name` - Get by department
- `POST /api/timetables` - Create timetable (admin)
- `PUT /api/timetables/:id` - Update timetable (admin)
- `DELETE /api/timetables/:id` - Delete timetable (admin)

### Announcements
- `GET /api/announcements` - Get all published announcements
- `GET /api/announcements/:id` - Get single announcement
- `GET /api/announcements/admin/all` - Get all announcements (admin)
- `POST /api/announcements` - Create announcement (admin)
- `PUT /api/announcements/:id` - Update announcement (admin)
- `DELETE /api/announcements/:id` - Delete announcement (admin)

## JSON Timetable Format

The timetables are stored and served in JSON format:

```json
{
  "department": "Computer Science",
  "schedule": [
    {
      "day": "Monday",
      "course": "Data Structures",
      "time": "10:00 - 12:00",
      "professor": "Dr. John Doe",
      "room": "A101"
    }
  ]
}
```

## Security Features

- JWT-based authentication with 30-day expiration
- Passwords hashed with bcrypt (10 rounds)
- Protected API routes with middleware
- No reviews or ratings system (no data manipulation risks)
- CORS configured for frontend access
- Environment variables for sensitive data

## Design Features

- **Responsive Design**: Mobile-first approach with breakpoints at 640px, 768px, 1024px
- **Modern UI**: CSS Grid, Flexbox, smooth transitions
- **Accessibility**: ARIA labels, keyboard navigation, focus visible states
- **Performance**: Minimal animations, optimized CSS with CSS variables
- **Card-based Layout**: Clean presentation of professor profiles

## Deployment

### Backend Deployment (e.g., Heroku, Railway)
```bash
# Set environment variables
heroku config:set MONGODB_URI=<your-mongodb-uri>
heroku config:set JWT_SECRET=<your-jwt-secret>
heroku config:set NODE_ENV=production

# Deploy
git push heroku main
```

### Frontend Deployment (e.g., Netlify, Vercel)
```bash
cd frontend
npm run build

# Deploy the build folder
```

## Development

### Running Tests
```bash
# Backend tests
cd backend
npm test

# Frontend tests
cd frontend
npm test
```

### Code Style
- ESLint for JavaScript/React
- Consistent naming conventions
- Comments for complex logic
- Modular code structure

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Support

For support, email admin@university.edu or join our Slack channel.

## Acknowledgments

- React team for the amazing framework
- Express.js team for the backend framework
- MongoDB team for the database
- All contributors and testers
