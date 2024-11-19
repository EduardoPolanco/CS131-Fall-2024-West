#include <iostream>
#include <vector>
#include <cmath>

using namespace std;

// Function to calculate Mean Squared Error (MSE)
double meanSquaredError(const vector<double>& x, const vector<double>& y, double m, double b) {
    double error = 0.0;
    int n = x.size();
    for (int i = 0; i < n; i++) {
        double prediction = m * x[i] + b;
        error += pow(prediction - y[i], 2);
    }
    return error / n;
}

// Gradient Descent function to update slope (m) and intercept (b)
void gradientDescent(const vector<double>& x, const vector<double>& y, double& m, double& b, double learningRate, int iterations) {
    int n = x.size();
    for (int i = 0; i < iterations; i++) {
        double mGradient = 0;
        double bGradient = 0;

        // Compute gradients for m and b
        for (int j = 0; j < n; j++) {
            double prediction = m * x[j] + b;
            mGradient += -2 * x[j] * (y[j] - prediction);
            bGradient += -2 * (y[j] - prediction);
        }

        // Update m and b using the gradients
        m -= (mGradient / n) * learningRate;
        b -= (bGradient / n) * learningRate;

        // Print the error to monitor progress
        if (i % 100 == 0) {
            cout << "Iteration " << i << " - Error: " << meanSquaredError(x, y, m, b) << endl;
        }
    }
}

// Main function to demonstrate linear regression
int main() {
    // Training data (x = input, y = output)
    vector<double> x = { 1, 2, 3, 4, 5 }; // Hours of study
    vector<double> y = { 2, 4, 6, 8, 10 }; // Test scores

    // Initial values for slope (m) and intercept (b)
    double m = 0.0; // slope
    double b = 0.0; // y-intercept

    // Hyperparameters for gradient descent
    double learningRate = 0.01;
    int iterations = 1000;

    cout << "Starting linear regression...\n";
    gradientDescent(x, y, m, b, learningRate, iterations);

    // Output final slope and intercept
    cout << "Final model: y = " << m << " * x + " << b << endl;

    // Use the model to make predictions
    double testInput = 6; // Predict for x = 6 (hours of study)
    double prediction = m * testInput + b;
    cout << "Predicted score for " << testInput << " hours of study: " << prediction << endl;

    return 0;
}
