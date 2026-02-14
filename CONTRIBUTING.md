# Contributing to FIX Security Platform

Thank you for your interest in contributing to the FIX Security Platform!

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/FIX-SECURITY.git`
3. Create a branch: `git checkout -b feature/your-feature-name`
4. Make your changes
5. Commit your changes: `git commit -m "Add your commit message"`
6. Push to your fork: `git push origin feature/your-feature-name`
7. Create a Pull Request

## Code Style

### Java
- Follow Java coding conventions
- Use meaningful variable and method names
- Add Javadoc comments for public methods
- Run `mvn checkstyle:check` before committing

### Python
- Follow PEP 8 style guide
- Use type hints where appropriate
- Add docstrings for functions and classes
- Run `flake8` before committing

## Testing

- Write unit tests for new features
- Ensure all tests pass before submitting PR
- Aim for >80% code coverage

## Commit Messages

- Use clear, descriptive commit messages
- Reference issue numbers if applicable
- Format: `type(scope): description`

Examples:
- `feat(parser): Add support for FIX 5.0SP2`
- `fix(security): Fix anomaly detection threshold`
- `docs(api): Update API documentation`

## Pull Request Process

1. Ensure your code follows the style guidelines
2. Update documentation if needed
3. Add tests for new features
4. Ensure all tests pass
5. Update CHANGELOG.md if applicable
6. Request review from maintainers

## Questions?

Feel free to open an issue for any questions or discussions.
