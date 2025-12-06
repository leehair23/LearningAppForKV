import Footer from "./components/Footer";
import Navbar from "./components/Navbar";

function App() {
  return (
    <>
      <div className="transition-all duration-300 w-full ">
        <Navbar />
        <section className="bg-white py-20 px-4">
          <div className="max-w-4xl mx-auto text-center">
            <h1 className="text-4xl md:text-6xl font-bold mb-6 text-gray-800">
              Welcome to Lode Keet
            </h1>

            <p className="text-xl md:text-2xl mb-8 text-gray-600">
              Practice and Reap success you want in Coding
            </p>

            <a
              href="#xxx"
              className="bg-primary-700 text-white px-8 py-4 rounded-lg font-semibold hover:bg-primary-800 transition-colors">
              Get Started Today
            </a>
          </div>
        </section>

        <section className="py-20 px-4 bg-gray-50">
          <div className="max-w-6xl mx-auto">
            <h2 className="text-3xl md:text-4xl font-bold text-center mb-12 text-gray-800">
              Why Choose Us
            </h2>
            <div className="px-6">
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-x-6 gap-y-6 text-lg tracking-tight">
                <div className="bg-white rounded-lg shadow-lg p-6">
                  <div className="flex flex-row">
                    <div className="pl-2 pr-6">
                      <div className="flex w-12 h-12 rounded-md bg-primary-700 text-gray-200 items-center justify-center">
                        <svg
                          className="w-6 h-6"
                          fill="currentColor"
                          viewBox="0 0 24 24">
                          <path d="M13 10V3L4 14h7v7l9-11h-7z"></path>
                        </svg>
                      </div>
                    </div>
                    <div className="flex flex-col">
                      <h3 className="text-xl font-semibold text-gray-800 pb-2">
                        Lightning Fast
                      </h3>
                      <p className="text-gray-700">
                        Experience blazing fast performance with our optimized
                        platform that delivers exceptional user experiences
                      </p>
                    </div>
                  </div>
                </div>
                <div className="bg-white rounded-lg shadow-lg p-6">
                  <div className="flex flex-row">
                    <div className="pl-2 pr-6">
                      <div className="flex w-12 h-12 rounded-md bg-primary-700 text-gray-200 items-center justify-center">
                        <svg
                          className="w-6 h-6"
                          fill="currentColor"
                          viewBox="0 0 24 24">
                          <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                        </svg>
                      </div>
                    </div>
                    <div className="flex flex-col">
                      <h3 className="text-xl font-semibold text-gray-800 pb-2">
                        Secure &amp; Reliable
                      </h3>
                      <p className="text-gray-700">
                        Your data is protected with enterprise-grade security
                        measures and continuous monitoring
                      </p>
                    </div>
                  </div>
                </div>
                <div className="bg-white rounded-lg shadow-lg p-6">
                  <div className="flex flex-row">
                    <div className="pl-2 pr-6">
                      <div className="flex w-12 h-12 rounded-md bg-primary-700 text-gray-200 items-center justify-center">
                        <svg
                          className="w-6 h-6"
                          fill="currentColor"
                          viewBox="0 0 24 24">
                          <path d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
                        </svg>
                      </div>
                    </div>
                    <div className="flex flex-col">
                      <h3 className="text-xl font-semibold text-gray-800 pb-2">
                        Responsive Design
                      </h3>
                      <p className="text-gray-700">
                        Optimized for all devices with a responsive design that
                        works perfectly on mobile and desktop
                      </p>
                    </div>
                  </div>
                </div>
                <div className="bg-white rounded-lg shadow-lg p-6">
                  <div className="flex flex-row">
                    <div className="pl-2 pr-6">
                      <div className="flex w-12 h-12 rounded-md bg-primary-700 text-gray-200 items-center justify-center">
                        <svg
                          className="w-6 h-6"
                          fill="currentColor"
                          viewBox="0 0 24 24">
                          <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                        </svg>
                      </div>
                    </div>
                    <div className="flex flex-col">
                      <h3 className="text-xl font-semibold text-gray-800 pb-2">
                        Loved by Users
                      </h3>
                      <p className="text-gray-700">
                        Join thousands of satisfied customers who trust our
                        solution for their business needs
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <Footer></Footer>
      </div>
    </>
  );
}

export default App;
